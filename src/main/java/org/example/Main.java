package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner =
            new Scanner(System.in);

    private static final List<Product> products =
            new ArrayList<>();

    public static void main(String[] args) {

        addSampleProducts();

        Customer customer =
                new Customer(
                        1,
                        "Ahmed",
                        "ahmed@gmail.com"
                );

        while (true) {

            showMenu();

            int choice =
                    readInt("Enter your choice: ");

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
                        customer.displayOrderHistory();
                        break;

                    case 8:
                        manageOrderStatus(customer);
                        break;

                    case 9:

                        System.out.println(
                                "Thank you for shopping with us! See you next time!"
                        );

                        scanner.close();
                        return;

                    default:

                        System.out.println(
                                "Invalid choice."
                        );
                }

            } catch (
                    ProductNotFoundException |
                    InsufficientStockException |
                    EmptyCartException e
            ) {

                System.out.println(
                        "Error: " + e.getMessage()
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Invalid input: " + e.getMessage()
                );

            } catch (IllegalStateException e) {

                System.out.println(
                        "Operation failed: " + e.getMessage()
                );
            }
        }
    }

    // =========================
    // MENU
    // =========================

    private static void showMenu() {

        System.out.println(
                "\n=========================="
        );

        System.out.println(
                "     E-COMMERCE SYSTEM"
        );

        System.out.println(
                "=========================="
        );

        System.out.println(
                "1. View Products"
        );

        System.out.println(
                "2. Add Product to Cart"
        );

        System.out.println(
                "3. View Cart"
        );

        System.out.println(
                "4. Update Cart Quantity"
        );

        System.out.println(
                "5. Remove Product"
        );

        System.out.println(
                "6. Checkout"
        );

        System.out.println(
                "7. View Order History"
        );

        System.out.println(
                "8. Manage Order Status"
        );

        System.out.println(
                "9. Exit"
        );

        System.out.println(
                "=========================="
        );
    }

    // =========================
    // SHOW PRODUCTS
    // =========================

    private static void showProducts() {

        System.out.println(
                "\n===== PRODUCTS ====="
        );

        for (Product product : products) {

            product.displayInfo();
        }
    }

    // =========================
    // ADD TO CART
    // =========================

    private static void addToCart(
            Customer customer
    )
            throws ProductNotFoundException,
            InsufficientStockException {

        int id =
                readInt("Enter Product ID: ");

        Product product =
                findProduct(id);

        if (product == null) {

            throw new ProductNotFoundException(
                    "Product not found."
            );
        }

        int quantity =
                readInt("Enter Quantity: ");

        customer.getCart().addProduct(
                product,
                quantity
        );
    }

    // =========================
    // UPDATE CART QUANTITY
    // =========================

    private static void updateCartQuantity(
            Customer customer
    )
            throws ProductNotFoundException,
            InsufficientStockException {

        if (customer.getCart().isEmpty()) {

            System.out.println(
                    "Your cart is empty."
            );

            return;
        }

        customer.getCart().displayCart();

        int productId =
                readInt("Enter Product ID: ");

        int newQuantity =
                readInt("Enter New Quantity: ");

        customer.getCart().updateQuantity(
                productId,
                newQuantity
        );
    }

    // =========================
    // REMOVE FROM CART
    // =========================

    private static void removeFromCart(
            Customer customer
    )
            throws ProductNotFoundException {

        if (customer.getCart().isEmpty()) {

            System.out.println(
                    "Your cart is empty."
            );

            return;
        }

        customer.getCart().displayCart();

        int id =
                readInt("Enter Product ID: ");

        customer.getCart().removeProduct(id);
    }

    // =========================
    // CHECKOUT
    // =========================

    private static void checkout(
            Customer customer
    )
            throws EmptyCartException {

        if (customer.getCart().isEmpty()) {

            throw new EmptyCartException(
                    "Cannot checkout. Cart is empty."
            );
        }

        /*
         * STEP 1
         * Validate stock before making
         * any changes.
         */

        for (CartItem item :
                customer.getCart().getItems()) {

            Product product =
                    item.getProduct();

            int requestedQuantity =
                    item.getQuantity();

            if (requestedQuantity >
                    product.getStock()) {

                throw new IllegalStateException(
                        "Insufficient stock for product: "
                                + product.getName()
                                + ". Available: "
                                + product.getStock()
                                + ", Requested: "
                                + requestedQuantity
                );
            }
        }

        /*
         * STEP 2
         * Create order snapshot.
         *
         * CartItem does NOT modify stock.
         */

        Order order =
                new Order(customer);

        /*
         * STEP 3
         * Reduce stock exactly ONCE.
         */

        for (CartItem item :
                customer.getCart().getItems()) {

            Product product =
                    item.getProduct();

            product.reduceStock(
                    item.getQuantity()
            );
        }

        /*
         * STEP 4
         * Add order to history.
         */

        customer.addOrder(order);

        /*
         * STEP 5
         * Display order.
         */

        order.displayOrder();

        /*
         * STEP 6
         * Clear cart.
         */

        customer.getCart().clear();

        System.out.println(
                "Order placed successfully!"
        );
    }

    // =========================
    // MANAGE ORDER STATUS
    // =========================

    private static void manageOrderStatus(
            Customer customer
    ) {

        List<Order> orders =
                customer.getOrders();

        if (orders.isEmpty()) {

            System.out.println(
                    "\nNo orders found."
            );

            return;
        }

        System.out.println(
                "\n===== ORDERS ====="
        );

        for (Order order : orders) {

            System.out.println(
                    "Order ID: "
                            + order.getOrderId()
                            + " | Status: "
                            + order.getStatus()
                            + " | Total: "
                            + order.getTotal()
            );
        }

        int orderId =
                readInt("Enter Order ID: ");

        Order selectedOrder = null;

        for (Order order : orders) {

            if (order.getOrderId() == orderId) {

                selectedOrder = order;
                break;
            }
        }

        if (selectedOrder == null) {

            System.out.println(
                    "Order not found."
            );

            return;
        }

        System.out.println(
                "\nCurrent Status: "
                        + selectedOrder.getStatus()
        );

        System.out.println(
                "\n1. Confirm Order"
        );

        System.out.println(
                "2. Ship Order"
        );

        System.out.println(
                "3. Deliver Order"
        );

        System.out.println(
                "4. Cancel Order"
        );

        System.out.println(
                "5. Back"
        );

        int choice =
                readInt("Enter your choice: ");

        try {

            switch (choice) {

                case 1:

                    selectedOrder.updateStatus(
                            OrderStatus.CONFIRMED
                    );

                    System.out.println(
                            "Order confirmed successfully."
                    );

                    break;

                case 2:

                    selectedOrder.updateStatus(
                            OrderStatus.SHIPPED
                    );

                    System.out.println(
                            "Order shipped successfully."
                    );

                    break;

                case 3:

                    selectedOrder.updateStatus(
                            OrderStatus.DELIVERED
                    );

                    System.out.println(
                            "Order delivered successfully."
                    );

                    break;

                case 4:

                    selectedOrder.updateStatus(
                            OrderStatus.CANCELLED
                    );

                    System.out.println(
                            "Order cancelled successfully."
                    );

                    break;

                case 5:

                    return;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }

        } catch (IllegalStateException e) {

            System.out.println(
                    "Cannot update order status: "
                            + e.getMessage()
            );
        }
    }

    // =========================
    // FIND PRODUCT
    // =========================

    private static Product findProduct(int id) {

        for (Product product : products) {

            if (product.getId() == id) {

                return product;
            }
        }

        return null;
    }

    // =========================
    // READ INTEGER
    // =========================

    private static int readInt(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    // =========================
    // SAMPLE PRODUCTS
    // =========================

    private static void addSampleProducts() {

        Product laptop =
                new Electronics(
                        1,
                        "Laptop",
                        30000,
                        5,
                        12
                );

        Product phone =
                new Electronics(
                        2,
                        "Smart Phone",
                        15000,
                        10,
                        24
                );

        Product shirt =
                new Clothing(
                        3,
                        "T-Shirt",
                        500,
                        20,
                        "L"
                );

        products.add(laptop);
        products.add(phone);
        products.add(shirt);
    }
}