USE [ECommerceSystem];
GO

IF OBJECT_ID(N'dbo.Products', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Products (
                              ProductId INT PRIMARY KEY,
                              ProductName NVARCHAR(100) NOT NULL,
                              Price DECIMAL(12, 2) NOT NULL CHECK (Price >= 0),
                              Stock INT NOT NULL CHECK (Stock >= 0),
                              ProductType VARCHAR(20) NOT NULL,
                              WarrantyMonths INT NULL,
                              ClothingSize NVARCHAR(10) NULL
);
END;
GOسسسسسسسسسسسسسسس

INSERT INTO dbo.Products
    (ProductId, ProductName, Price, Stock, ProductType, WarrantyMonths, ClothingSize)
SELECT v.ProductId, v.ProductName, v.Price, v.Stock,
       v.ProductType, v.WarrantyMonths, v.ClothingSize
FROM (VALUES
          (1, N'Laptop',      30000.00, 5,  'Electronics', 12, NULL),
          (2, N'Smart Phone', 15000.00, 10, 'Electronics', 24, NULL),
          (3, N'T-Shirt',       500.00, 20, 'Clothing',    NULL, N'L')
     ) AS v (ProductId, ProductName, Price, Stock, ProductType, WarrantyMonths, ClothingSize)
WHERE NOT EXISTS (
    SELECT 1 FROM dbo.Products AS p WHERE p.ProductId = v.ProductId
);