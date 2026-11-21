USE [ECommerceSystem];
GO

SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF OBJECT_ID(N'dbo.Customers', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Customers (
                               CustomerId INT PRIMARY KEY,
                               CustomerName NVARCHAR(100) NOT NULL,
                               Email NVARCHAR(254) NOT NULL UNIQUE
);
END;

INSERT INTO dbo.Customers (CustomerId, CustomerName, Email)
SELECT v.CustomerId, v.CustomerName, v.Email
FROM (VALUES
          (1, N'Ahmed', N'ahmed@example.com'),
          (2, N'Nada',  N'nada@example.com'),
          (3, N'Amr',   N'amr@example.com')
     ) AS v (CustomerId, CustomerName, Email)
WHERE NOT EXISTS (
    SELECT 1
    FROM dbo.Customers AS c
    WHERE c.CustomerId = v.CustomerId
);

IF OBJECT_ID(N'dbo.FK_Orders_Customers', N'F') IS NULL
BEGIN
ALTER TABLE dbo.Orders WITH CHECK
    ADD CONSTRAINT FK_Orders_Customers
    FOREIGN KEY (CustomerId) REFERENCES dbo.Customers(CustomerId);
END;

COMMIT TRANSACTION;
GO