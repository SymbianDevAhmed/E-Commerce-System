package org.example;

public class Electronics extends Product {

    private int warrantyMonths;

    public Electronics(
            int id,
            String name,
            double price,
            int stock,
            int warrantyMonths) {

        super(id, name, price, stock);

        if (warrantyMonths < 0) {
            throw new IllegalArgumentException(
                    "Warranty months cannot be negative."
            );
        }

        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    @Override
    public double calculateDiscount() {
        return getPrice() * 0.10;
    }

    @Override
    public void displayInfo() {

        super.displayInfo();

        System.out.println(
                "Warranty: " + warrantyMonths + " months"
        );
    }
}