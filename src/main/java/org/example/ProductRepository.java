package org.example;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

final class ProductRepository {

    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=ECommerceSystem;"
            + "integratedSecurity=true;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";

    static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    static List<Product> loadProducts() throws SQLException {
        String sql = """
                SELECT ProductId, ProductName, Price, Stock, ProductType,
                       WarrantyMonths, ClothingSize
                FROM dbo.Products
                ORDER BY ProductId
                """;

        List<Product> products = new ArrayList<>();

        try (var connection = DriverManager.getConnection(URL);
             var statement = connection.createStatement();
             var rows = statement.executeQuery(sql)) {
            while (rows.next()) {
                int id = rows.getInt("ProductId");
                String name = rows.getString("ProductName");
                double price = rows.getDouble("Price");
                int stock = rows.getInt("Stock");
                String type = rows.getString("ProductType");

                Product product = switch (type) {
                    case "Electronics" -> new Electronics(
                            id, name, price, stock, rows.getInt("WarrantyMonths"));
                    case "Clothing" -> new Clothing(
                            id, name, price, stock, rows.getString("ClothingSize"));
                    default -> throw new SQLException("Unknown product type: " + type);
                };

                products.add(product);
            }
        }

        return products;
    }
}