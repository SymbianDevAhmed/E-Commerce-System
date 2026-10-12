package org.example;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private int id;
    private String name;
    private String email;

    private Cart cart;
    private List<Order> orders;

    // =========================
    // CONSTRUCTOR
    // =========================

    public Customer(
            int id,
            String name,
            String email
    ) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than zero."
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer name cannot be empty."
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer email cannot be empty."
            );
        }

        this.id = id;
        this.name = name;
        this.email = email;

        this.cart = new Cart();
        this.orders = new ArrayList<>();
    }

    // =========================
    // GETTERS
    // =========================

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Cart getCart() {
        return cart;
    }

    // =========================
    // ADD ORDER
    // =========================

    public void addOrder(Order order) {

        if (order == null) {
            throw new IllegalArgumentException(
                    "Order cannot be null."
            );
        }

        orders.add(order);
    }

    // =========================
    // GET ORDERS
    // =========================

    public List<Order> getOrders() {

        return new ArrayList<>(orders);
    }

    // =========================
    // DISPLAY ORDER HISTORY
    // =========================

    public void displayOrderHistory() {

        System.out.println(
                "\n===== ORDER HISTORY ====="
        );

        if (orders.isEmpty()) {

            System.out.println(
                    "No orders found."
            );

            System.out.println(
                    "========================"
            );

            return;
        }

        for (Order order : orders) {

            System.out.println(
                    "Order ID : " + order.getOrderId()
            );

            System.out.println(
                    "Customer : " + order.getCustomer().getName()
            );

            System.out.println(
                    "Total    : " + order.getTotal()
            );

            System.out.println(
                    "-------------------------"
            );
        }

        System.out.println(
                "Total Orders: " + orders.size()
        );

        System.out.println(
                "========================"
        );
    }
}