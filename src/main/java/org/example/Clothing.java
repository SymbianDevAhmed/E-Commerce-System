package org.example;

public class Clothing extends Product {

    private String size;

    public Clothing(
            int id,
            String name,
            double price,
            int stock,
            String size) {

        super(id, name, price, stock);

        if (size == null || size.isBlank()) {
            throw new IllegalArgumentException(
                    "Size cannot be empty."
            );
        }

        this.size = size;
    }

    public String getSize() {
        return size;
    }

    @Override
    public double calculateDiscount() {
        return getPrice() * 0.15;
    }

    @Override
    public void displayInfo() {

        super.displayInfo();

        System.out.println(
                "Size: " + size
        );
    }
}