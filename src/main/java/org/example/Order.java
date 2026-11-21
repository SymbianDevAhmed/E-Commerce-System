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

    public Order(Customer customer) throws EmptyCartException {
        this(customer, null);
    }

    Order(Customer customer, Integer databaseOrderId) throws EmptyCartException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }

        if (customer.getCart().isEmpty()) {
            throw new EmptyCartException("Cannot create an order from an empty cart.");
        }

        this.orderId = databaseOrderId == null ? nextId++ : databaseOrderId;
        this.customer = customer;
        this.items = new ArrayList<>();

        // Take a snapshot of the cart items and their quantities.
        for (CartItem item : customer.getCart().getItems()) {
            this.items.add(new CartItem(item.getProduct(), item.getQuantity()));
        }

        this.total = calculateTotal();
        this.status = OrderStatus.PENDING;
    }

    private double calculateTotal() {
        double total = 0;

        for (CartItem item : items) {
            total += item.getTotalPrice();
        }

        return total;
    }

    public int getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public double getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void updateStatus(OrderStatus newStatus) {
        validateStatusTransition(status, newStatus);
        status = newStatus;
    }

    static void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }

        boolean allowed = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED
                    || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.SHIPPED
                    || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalStateException(
                    "Invalid order status transition: " + currentStatus + " -> " + newStatus
            );
        }
    }

    public void cancelOrder() {
        updateStatus(OrderStatus.CANCELLED);
    }

    public void displayOrder() {
        System.out.println("\n===== ORDER =====");
        System.out.println("Order ID: " + orderId);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Status: " + status);
        System.out.println("\nItems:");

        for (CartItem item : items) {
            item.displayItem();
        }

        System.out.println("Total: " + total);
        System.out.println("=================");
    }
}