package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CartTest {

    @Test
    void calculatesTotalForTwoLaptopsAfterDiscount() throws InsufficientStockException {
        Cart cart = new Cart();
        Product laptop = new Electronics(1, "Laptop", 30000, 5, 12);

        cart.addProduct(laptop, 2);

        assertEquals(54000.0, cart.calculateTotal(), 0.01);
    }

    @Test
    void rejectsQuantityGreaterThanStock() {
        Cart cart = new Cart();
        Product laptop = new Electronics(1, "Laptop", 30000, 5, 12);

        assertThrows(InsufficientStockException.class, () -> cart.addProduct(laptop, 6));
        assertTrue(cart.isEmpty());
    }
}
