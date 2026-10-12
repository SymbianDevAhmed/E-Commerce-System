package org.example;

public abstract class Product {

    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(
            int id,
            String name,
            double price,
            int stock) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Product ID must be greater than zero."
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty."
            );
        }

        if (price < 0) {
            throw new IllegalArgumentException(
                    "Product price cannot be negative."
            );
        }

        if (stock < 0) {
            throw new IllegalArgumentException(
                    "Product stock cannot be negative."
            );
        }

        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
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

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    // =========================
    // SET STOCK
    // =========================

    public void setStock(int stock) {

        if (stock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative."
            );
        }

        this.stock = stock;
    }

    // =========================
    // REDUCE STOCK
    // =========================

    public void reduceStock(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        if (quantity > stock) {
            throw new IllegalArgumentException(
                    "Not enough stock for " + name
            );
        }

        stock -= quantity;
    }

    // =========================
    // INCREASE STOCK
    // =========================

    public void increaseStock(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        stock += quantity;
    }

    // =========================
    // DISCOUNT
    // =========================

    public abstract double calculateDiscount();

    // =========================
    // FINAL PRICE
    // =========================

    public double getFinalPrice() {

        return price - calculateDiscount();
    }

    // =========================
    // DISPLAY PRODUCT
    // =========================

    public void displayInfo() {

        System.out.println("-------------------------");
        System.out.println("ID          : " + id);
        System.out.println("Name        : " + name);
        System.out.println("Price       : " + price);
        System.out.println("Stock       : " + stock);
        System.out.println(
                "Discount    : " + calculateDiscount()
        );
        System.out.println(
                "Final Price : " + getFinalPrice()
        );
        System.out.println("-------------------------");
    }
}