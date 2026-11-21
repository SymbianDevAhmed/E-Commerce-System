package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Map;

public class ApiServer {

    private static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 8080), 0
        );

        server.createContext("/products", ApiServer::handleProducts);
        server.createContext("/orders", ApiServer::handleOrders);
        server.createContext("/checkout", ApiServer::handleCheckout);
        server.start();

        System.out.println("API running at http://127.0.0.1:8080");
    }

    private static void handleProducts(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"/products".equals(exchange.getRequestURI().getPath())) {
                sendJson(exchange, 404, Map.of("error", "Route not found"));
                return;
            }

            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                sendJson(exchange, 405, Map.of("error", "Use GET for this endpoint"));
                return;
            }

            try {
                var products = ProductRepository.loadProducts().stream()
                        .map(product -> new ProductResponse(
                                product.getId(),
                                product.getName(),
                                product.getPrice(),
                                product.getFinalPrice(),
                                product.getStock()
                        ))
                        .toList();

                sendJson(exchange, 200, products);
            } catch (SQLException e) {
                System.err.println("Could not load products: " + e.getMessage());
                sendJson(exchange, 500, Map.of("error", "Could not load products"));
            }
        }
    }

    private static void handleOrders(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"/orders".equals(exchange.getRequestURI().getPath())) {
                sendJson(exchange, 404, Map.of("error", "Route not found"));
                return;
            }

            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                sendJson(exchange, 405, Map.of("error", "Use GET for this endpoint"));
                return;
            }

            String query = exchange.getRequestURI().getRawQuery();
            if (query == null || !query.matches("customerId=[1-9][0-9]*")) {
                sendJson(exchange, 400, Map.of("error", "Provide a positive customerId"));
                return;
            }

            int customerId;
            try {
                customerId = Integer.parseInt(query.substring("customerId=".length()));
            } catch (NumberFormatException e) {
                sendJson(exchange, 400, Map.of("error", "Invalid customerId"));
                return;
            }

            try {
                var orders = OrderRepository.findByCustomer(customerId);
                sendJson(exchange, 200, orders);
            } catch (SQLException e) {
                System.err.println("Could not load orders: " + e.getMessage());
                sendJson(exchange, 500, Map.of("error", "Could not load orders"));
            }
        }
    }

    private static void handleCheckout(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"/checkout".equals(exchange.getRequestURI().getPath())) {
                sendJson(exchange, 404, Map.of("error", "Route not found"));
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "POST");
                sendJson(exchange, 405, Map.of("error", "Use POST for this endpoint"));
                return;
            }

            byte[] body = exchange.getRequestBody().readNBytes(4097);
            if (body.length > 4096) {
                sendJson(exchange, 413, Map.of("error", "Request body is too large"));
                return;
            }

            JsonNode request;
            try {
                request = JSON.readTree(body);
            } catch (JsonProcessingException e) {
                sendJson(exchange, 400, Map.of("error", "Invalid JSON"));
                return;
            }

            if (request == null
                    || !isPositiveInt(request.path("customerId"))
                    || !isPositiveInt(request.path("productId"))
                    || !isPositiveInt(request.path("quantity"))) {
                sendJson(exchange, 400, Map.of(
                        "error", "customerId, productId and quantity must be positive integers"
                ));
                return;
            }

            int customerId = request.path("customerId").intValue();
            int productId = request.path("productId").intValue();
            int quantity = request.path("quantity").intValue();

            try {
                Customer customer = findCheckoutCustomer(customerId);
                if (customer == null) {
                    sendJson(exchange, 404, Map.of("error", "Customer not found"));
                    return;
                }

                Product product = ProductRepository.loadProducts().stream()
                        .filter(item -> item.getId() == productId)
                        .findFirst()
                        .orElse(null);

                if (product == null) {
                    sendJson(exchange, 404, Map.of("error", "Product not found"));
                    return;
                }

                customer.getCart().addProduct(product, quantity);
                Order order = OrderRepository.saveCheckout(customer);

                sendJson(exchange, 201, Map.of(
                        "orderId", order.getOrderId(),
                        "customerId", customerId,
                        "status", order.getStatus().name(),
                        "total", order.getTotal()
                ));
            } catch (InsufficientStockException e) {
                sendJson(exchange, 409, Map.of("error", "Insufficient stock"));
            } catch (EmptyCartException | SQLException e) {
                System.err.println("Checkout failed: " + e.getMessage());
                sendJson(exchange, 500, Map.of("error", "Could not complete checkout"));
            }
        }
    }

    private static boolean isPositiveInt(JsonNode value) {
        return value.isIntegralNumber() && value.canConvertToInt() && value.intValue() > 0;
    }

    private static Customer findCheckoutCustomer(int customerId) throws SQLException {
        String sql = """
                SELECT CustomerName, Email
                FROM dbo.Customers
                WHERE CustomerId = ?
                """;

        try (var connection = ProductRepository.openConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);

            try (var rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return null;
                }

                return new Customer(
                        customerId,
                        rows.getString("CustomerName"),
                        rows.getString("Email")
                );
            }
        }
    }

    private static void sendJson(HttpExchange exchange, int status, Object body)
            throws IOException {
        byte[] json = JSON.writeValueAsBytes(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, json.length);
        exchange.getResponseBody().write(json);
    }

    public record ProductResponse(
            int id, String name, double price, double finalPrice, int stock
    ) {}
}