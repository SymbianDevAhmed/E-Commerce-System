USE [ECommerceSystem];
GO

IF OBJECT_ID(N'dbo.Orders', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Orders (
                            OrderId INT IDENTITY(1, 1) PRIMARY KEY,
                            CustomerId INT NOT NULL,
                            OrderStatus VARCHAR(20) NOT NULL
                                CHECK (OrderStatus IN (
                                                       'PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'
                                    )),
                            Total DECIMAL(14, 2) NOT NULL CHECK (Total >= 0),
                            CreatedAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);
END;
GO

IF OBJECT_ID(N'dbo.OrderItems', N'U') IS NULL
BEGIN
CREATE TABLE dbo.OrderItems (
                                OrderId INT NOT NULL REFERENCES dbo.Orders(OrderId),
                                ProductId INT NOT NULL REFERENCES dbo.Products(ProductId),
                                Quantity INT NOT NULL CHECK (Quantity > 0),
                                UnitPrice DECIMAL(12, 2) NOT NULL CHECK (UnitPrice >= 0),
                                PRIMARY KEY (OrderId, ProductId)
);
END;
GO