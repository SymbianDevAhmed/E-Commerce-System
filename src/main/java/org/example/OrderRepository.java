package org.example;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

final class OrderRepository {

    static Order saveCheckout(Customer customer)
            throws SQLException, EmptyCartException {
        Cart cart = customer.getCart();
        if (cart.isEmpty()) {
            throw new EmptyCartException("Cannot checkout. Cart is empty.");
        }

        String insertOrder = """
                INSERT INTO dbo.Orders (CustomerId, OrderStatus, Total)
                OUTPUT INSERTED.OrderId
                VALUES (?, 'PENDING', ?)
                """;

        String updateStock = """
                UPDATE dbo.Products
                SET Stock = Stock - ?
                OUTPUT INSERTED.Stock
                WHERE ProductId = ? AND Stock >= ?
                """;

        String insertItem = """
                INSERT INTO dbo.OrderItems (OrderId, ProductId, Quantity, UnitPrice)
                VALUES (?, ?, ?, ?)
                """;

        Map<Product, Integer> remainingStock = new LinkedHashMap<>();
        Order order;

        try (var connection = ProductRepository.openConnection()) {
            connection.setAutoCommit(false);

            try (var orderStatement = connection.prepareStatement(insertOrder);
                 var stockStatement = connection.prepareStatement(updateStock);
                 var itemStatement = connection.prepareStatement(insertItem)) {

                orderStatement.setInt(1, customer.getId());
                orderStatement.setBigDecimal(2, BigDecimal.valueOf(cart.calculateTotal()));

                int orderId;
                try (var rows = orderStatement.executeQuery()) {
                    if (!rows.next()) {
                        throw new SQLException("SQL Server did not return an order ID.");
                    }
                    orderId = rows.getInt(1);
                }

                order = new Order(customer, orderId);

                for (CartItem item : order.getItems()) {
                    Product product = item.getProduct();
                    int quantity = item.getQuantity();

                    stockStatement.setInt(1, quantity);
                    stockStatement.setInt(2, product.getId());
                    stockStatement.setInt(3, quantity);

                    try (var rows = stockStatement.executeQuery()) {
                        if (!rows.next()) {
                            throw new SQLException("Insufficient stock for " + product.getName());
                        }
                        remainingStock.put(product, rows.getInt(1));
                    }

                    itemStatement.setInt(1, orderId);
                    itemStatement.setInt(2, product.getId());
                    itemStatement.setInt(3, quantity);
                    itemStatement.setBigDecimal(4, BigDecimal.valueOf(product.getFinalPrice()));
                    itemStatement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException | EmptyCartException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }

        remainingStock.forEach(Product::setStock);
        return order;
    }

    record OrderSummary(int id, String status, BigDecimal total) {}

    static List<OrderSummary> findByCustomer(int customerId) throws SQLException {
        String sql = """
            SELECT OrderId, OrderStatus, Total
            FROM dbo.Orders
            WHERE CustomerId = ?
            ORDER BY OrderId DESC
            """;

        List<OrderSummary> orders = new ArrayList<>();

        try (var connection = ProductRepository.openConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);

            try (var rows = statement.executeQuery()) {
                while (rows.next()) {
                    orders.add(new OrderSummary(
                            rows.getInt("OrderId"),
                            rows.getString("OrderStatus"),
                            rows.getBigDecimal("Total")
                    ));
                }
            }
        }

        return orders;
    }

    static void updateStatus(int customerId, OrderSummary order, OrderStatus newStatus)
            throws SQLException {
        OrderStatus currentStatus = OrderStatus.valueOf(order.status());
        Order.validateStatusTransition(currentStatus, newStatus);

        String sql = """
            UPDATE dbo.Orders
            SET OrderStatus = ?
            WHERE OrderId = ? AND CustomerId = ? AND OrderStatus = ?
            """;

        try (var connection = ProductRepository.openConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus.name());
            statement.setInt(2, order.id());
            statement.setInt(3, customerId);
            statement.setString(4, currentStatus.name());

            if (statement.executeUpdate() != 1) {
                throw new IllegalStateException("Order changed. Open Manage Order Status again.");
            }
        }
    }
}