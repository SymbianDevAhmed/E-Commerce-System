package org.example;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private static int nextId = 1;

    private final int orderId;
    private final Customer customer;
    private final List<CartItem> items;
    private final double total;
    private OrderStatus status;

    // =========================
    // CONSTRUCTOR
    // =========================

    public Order(Customer customer) throws EmptyCartException {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null."
            );
        }

        if (customer.getCart().isEmpty()) {
            throw new EmptyCartException(
                    "Cannot create an order from an empty cart."
            );
        }

        this.orderId = nextId++;
        this.customer = customer;

        // Create a snapshot of the cart
        this.items = new ArrayList<>();

        for (CartItem item : customer.getCart().getItems()) {

            this.items.add(
                    new CartItem(
                            item.getProduct(),
                            item.getQuantity()
                    )
            );
        }

        this.total = calculateTotal();

        // Every new order starts as PENDING
        this.status = OrderStatus.PENDING;
    }

    // =========================
    // CALCULATE TOTAL
    // =========================

    private double calculateTotal() {

        double total = 0;

        for (CartItem item : items) {
            total += item.getTotalPrice();
        }

        return total;
    }

    // =========================
    // GET ORDER ID
    // =========================

    public int getOrderId() {
        return orderId;
    }

    // =========================
    // GET CUSTOMER
    // =========================

    public Customer getCustomer() {
        return customer;
    }

    // =========================
    // GET ITEMS
    // =========================

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    // =========================
    // GET TOTAL
    // =========================

    public double getTotal() {
        return total;
    }

    // =========================
    // GET STATUS
    // =========================

    public OrderStatus getStatus() {
        return status;
    }

    // =========================
    // UPDATE ORDER STATUS
    // =========================

    public void updateStatus(OrderStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Order status cannot be null."
            );
        }

        switch (status) {

            case PENDING:

                if (newStatus == OrderStatus.CONFIRMED ||
                        newStatus == OrderStatus.CANCELLED) {

                    status = newStatus;
                    return;
                }

                break;

            case CONFIRMED:

                if (newStatus == OrderStatus.SHIPPED ||
                        newStatus == OrderStatus.CANCELLED) {

                    status = newStatus;
                    return;
                }

                break;

            case SHIPPED:

                if (newStatus == OrderStatus.DELIVERED) {

                    status = newStatus;
                    return;
                }

                break;

            case DELIVERED:
            case CANCELLED:

                break;
        }

        throw new IllegalStateException(
                "Invalid order status transition: "
                        + status
                        + " -> "
                        + newStatus
        );
    }

    // =========================
    // CANCEL ORDER
    // =========================

    public void cancelOrder() {

        updateStatus(OrderStatus.CANCELLED);
    }

    // =========================
    // DISPLAY ORDER
    // =========================

    public void displayOrder() {

        System.out.println("\n===== ORDER =====");

        System.out.println(
                "Order ID: " + orderId
        );

        System.out.println(
                "Customer: " + customer.getName()
        );

        System.out.println(
                "Status: " + status
        );

        System.out.println("\nItems:");

        for (CartItem item : items) {
            item.displayItem();
        }

        System.out.println(
                "Total: " + total
        );

        System.out.println(
                "================="
        );
    }
}