package org.example;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private final List<CartItem> items;

    // =========================
    // CONSTRUCTOR
    // =========================

    public Cart() {
        items = new ArrayList<>();
    }

    // =========================
    // ADD PRODUCT
    // =========================

    public void addProduct(
            Product product,
            int quantity
    ) throws InsufficientStockException {

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

        if (quantity > product.getStock()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: "
                            + product.getStock()
            );
        }

        for (CartItem item : items) {

            if (item.getProduct().getId() == product.getId()) {

                int newQuantity =
                        item.getQuantity() + quantity;

                if (newQuantity > product.getStock()) {
                    throw new InsufficientStockException(
                            "Insufficient stock. Available: "
                                    + product.getStock()
                    );
                }

                item.setQuantity(newQuantity);

                System.out.println(
                        "Product quantity updated in cart."
                );

                return;
            }
        }

        items.add(
                new CartItem(
                        product,
                        quantity
                )
        );

        System.out.println(
                "Product added to cart successfully."
        );
    }

    // =========================
    // REMOVE PRODUCT
    // =========================

    public void removeProduct(int productId)
            throws ProductNotFoundException {

        CartItem itemToRemove = null;

        for (CartItem item : items) {

            if (item.getProduct().getId() == productId) {

                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove == null) {
            throw new ProductNotFoundException(
                    "Product not found in cart."
            );
        }

        items.remove(itemToRemove);

        System.out.println(
                "Product removed from cart."
        );
    }

    // =========================
    // UPDATE PRODUCT QUANTITY
    // =========================

    public void updateQuantity(
            int productId,
            int newQuantity
    )
            throws ProductNotFoundException,
            InsufficientStockException {

        if (newQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        for (CartItem item : items) {

            if (item.getProduct().getId() == productId) {

                if (newQuantity >
                        item.getProduct().getStock()) {

                    throw new InsufficientStockException(
                            "Insufficient stock. Available: "
                                    + item.getProduct().getStock()
                    );
                }

                item.setQuantity(newQuantity);

                System.out.println(
                        "Product quantity updated successfully."
                );

                return;
            }
        }

        throw new ProductNotFoundException(
                "Product not found in cart."
        );
    }

    // =========================
    // CALCULATE TOTAL
    // =========================

    public double calculateTotal() {

        double total = 0;

        for (CartItem item : items) {
            total += item.getTotalPrice();
        }

        return total;
    }

    // =========================
    // DISPLAY CART
    // =========================

    public void displayCart() {

        System.out.println(
                "\n===== SHOPPING CART ====="
        );

        if (items.isEmpty()) {

            System.out.println(
                    "Your cart is empty."
            );

            System.out.println(
                    "========================="
            );

            return;
        }

        for (CartItem item : items) {
            item.displayItem();
        }

        System.out.println(
                "Cart Total: " + calculateTotal()
        );

        System.out.println(
                "========================="
        );
    }

    // =========================
    // GET ITEMS
    // =========================

    public List<CartItem> getItems() {

        return new ArrayList<>(items);
    }

    // =========================
    // CHECK IF EMPTY
    // =========================

    public boolean isEmpty() {

        return items.isEmpty();
    }

    // =========================
    // CLEAR CART
    // =========================

    public void clear() {

        items.clear();
    }
}