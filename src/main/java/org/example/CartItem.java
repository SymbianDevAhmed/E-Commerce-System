package org.example;

public class CartItem {

    private Product product;
    private int quantity;

    // =========================
    // CONSTRUCTOR
    // =========================

    public CartItem(Product product, int quantity) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null."
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        this.product = product;
        this.quantity = quantity;
    }

    // =========================
    // GET PRODUCT
    // =========================

    public Product getProduct() {
        return product;
    }

    // =========================
    // GET QUANTITY
    // =========================

    public int getQuantity() {
        return quantity;
    }

    // =========================
    // SET QUANTITY
    // =========================

    public void setQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        this.quantity = quantity;
    }

    // =========================
    // GET TOTAL PRICE
    // =========================

    public double getTotalPrice() {

        return product.getFinalPrice() * quantity;
    }

    // =========================
    // DISPLAY ITEM
    // =========================

    public void displayItem() {

        System.out.println(
                "Product  : " + product.getName()
        );

        System.out.println(
                "Quantity : " + quantity
        );

        System.out.println(
                "Price    : " + product.getFinalPrice()
        );

        System.out.println(
                "Subtotal : " + getTotalPrice()
        );

        System.out.println(
                "------------------"
        );
    }
}