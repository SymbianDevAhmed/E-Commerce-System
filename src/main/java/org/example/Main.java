package org.example;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Product> products = new ArrayList<>();

    public static void main(String[] args) {
        try {
            products.addAll(ProductRepository.loadProducts());
        } catch (SQLException e) {
            System.err.println("Could not load products: " + e.getMessage());
            return;
        }

        Customer customer;
        try {
            customer = selectCustomer();
        } catch (SQLException e) {
            System.err.println("Could not load customers: " + e.getMessage());
            return;
        }

        if (customer == null) {
            return;
        }

        while (true) {
            showMenu();
            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        showProducts();
                        break;
                    case 2:
                        addToCart(customer);
                        break;
                    case 3:
                        customer.getCart().displayCart();
                        break;
                    case 4:
                        updateCartQuantity(customer);
                        break;
                    case 5:
                        removeFromCart(customer);
                        break;
                    case 6:
                        checkout(customer);
                        break;
                    case 7:
                        displaySavedOrders(customer);
                        break;
                    case 8:
                        manageOrderStatus(customer);
                        break;
                    case 9:
                        System.out.println("Thank you for shopping with us! See you next time!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (ProductNotFoundException | InsufficientStockException | EmptyCartException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static Customer selectCustomer() throws SQLException {
        String sql = """
                SELECT CustomerId, CustomerName, Email
                FROM dbo.Customers
                ORDER BY CustomerId
                """;

        List<Customer> customers = new ArrayList<>();

        try (var connection = ProductRepository.openConnection();
             var statement = connection.prepareStatement(sql);
             var rows = statement.executeQuery()) {
            while (rows.next()) {
                customers.add(new Customer(
                        rows.getInt("CustomerId"),
                        rows.getString("CustomerName"),
                        rows.getString("Email")
                ));
            }
        }

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return null;
        }

        System.out.println("\n===== SELECT CUSTOMER =====");

        for (Customer customer : customers) {
            System.out.println(customer.getId() + ". " + customer.getName());
        }

        while (true) {
            int id = readInt("Enter Customer ID: ");

            for (Customer customer : customers) {
                if (customer.getId() == id) {
                    return customer;
                }
            }

            System.out.println("Customer not found. Try again.");
        }
    }

    private static void showMenu() {
        System.out.println("\n==========================");
        System.out.println("     E-COMMERCE SYSTEM");
        System.out.println("==========================");
        System.out.println("1. View Products");
        System.out.println("2. Add Product to Cart");
        System.out.println("3. View Cart");
        System.out.println("4. Update Cart Quantity");
        System.out.println("5. Remove Product");
        System.out.println("6. Checkout");
        System.out.println("7. View Order History");
        System.out.println("8. Manage Order Status");
        System.out.println("9. Exit");
        System.out.println("==========================");
    }

    private static void showProducts() {
        System.out.println("\n===== PRODUCTS =====");

        for (Product product : products) {
            product.displayInfo();
        }
    }

    private static void addToCart(Customer customer)
            throws ProductNotFoundException, InsufficientStockException {
        int id = readInt("Enter Product ID: ");
        Product product = findProduct(id);

        if (product == null) {
            throw new ProductNotFoundException("Product not found.");
        }

        int quantity = readInt("Enter Quantity: ");
        customer.getCart().addProduct(product, quantity);
    }

    private static void updateCartQuantity(Customer customer)
            throws ProductNotFoundException, InsufficientStockException {
        if (customer.getCart().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        customer.getCart().displayCart();

        int productId = readInt("Enter Product ID: ");
        int newQuantity = readInt("Enter New Quantity: ");
        customer.getCart().updateQuantity(productId, newQuantity);
    }

    private static void removeFromCart(Customer customer)
            throws ProductNotFoundException {
        if (customer.getCart().isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }

        customer.getCart().displayCart();

        int id = readInt("Enter Product ID: ");
        customer.getCart().removeProduct(id);
    }

    private static void checkout(Customer customer) throws EmptyCartException {
        Order order;

        try {
            order = OrderRepository.saveCheckout(customer);
        } catch (SQLException e) {
            System.err.println("Checkout could not be completed: " + e.getMessage());
            return;
        }

        customer.addOrder(order);
        customer.getCart().clear();
        order.displayOrder();

        System.out.println("Order placed successfully!");
    }

    private static void manageOrderStatus(Customer customer) {
        try {
            var orders = OrderRepository.findByCustomer(customer.getId());

            if (orders.isEmpty()) {
                System.out.println("No orders found.");
                return;
            }

            System.out.println("\n===== ORDERS =====");

            for (var order : orders) {
                System.out.println("Order ID: " + order.id()
                        + " | Status: " + order.status()
                        + " | Total: " + order.total().toPlainString());
            }

            int orderId = readInt("Enter Order ID: ");
            OrderRepository.OrderSummary selectedOrder = null;

            for (var order : orders) {
                if (order.id() == orderId) {
                    selectedOrder = order;
                    break;
                }
            }

            if (selectedOrder == null) {
                System.out.println("Order not found.");
                return;
            }

            System.out.println("\nCurrent Status: " + selectedOrder.status());
            System.out.println("1. Confirm Order");
            System.out.println("2. Ship Order");
            System.out.println("3. Deliver Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. Back");

            int choice = readInt("Enter your choice: ");

            if (choice == 5) {
                return;
            }

            OrderStatus newStatus = switch (choice) {
                case 1 -> OrderStatus.CONFIRMED;
                case 2 -> OrderStatus.SHIPPED;
                case 3 -> OrderStatus.DELIVERED;
                case 4 -> OrderStatus.CANCELLED;
                default -> null;
            };

            if (newStatus == null) {
                System.out.println("Invalid choice.");
                return;
            }

            OrderRepository.updateStatus(customer.getId(), selectedOrder, newStatus);
            System.out.println("Order status updated to " + newStatus + ".");
        } catch (IllegalStateException e) {
            System.out.println("Cannot update order status: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Could not manage order status: " + e.getMessage());
        }
    }

    private static Product findProduct(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }

        return null;
    }

    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void displaySavedOrders(Customer customer) {
        try {
            var orders = OrderRepository.findByCustomer(customer.getId());

            if (orders.isEmpty()) {
                System.out.println("No orders found.");
                return;
            }

            System.out.println("\n===== ORDER HISTORY =====");

            for (var order : orders) {
                System.out.println("Order ID: " + order.id()
                        + " | Status: " + order.status()
                        + " | Total: " + order.total().toPlainString());
            }
        } catch (SQLException e) {
            System.err.println("Could not load order history: " + e.getMessage());
        }
    }
}