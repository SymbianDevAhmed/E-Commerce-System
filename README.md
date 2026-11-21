# 🛒 E-Commerce System – Java

<p align="center">
  A Java e-commerce application with a console interface, SQL Server persistence, and a local JSON API.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Maven-Build_Tool-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/SQL_Server-Database-CC2927?style=for-the-badge" alt="SQL Server"/>
  <img src="https://img.shields.io/badge/OOP-Object--Oriented_Programming-3776AB?style=for-the-badge" alt="OOP"/>
</p>

---

## 📌 About the Project

The **E-Commerce System** is a diploma project developed to practise Java, Object-Oriented Programming (OOP), database integration, and software testing.

Users can select a sample customer, browse products, manage a shopping cart, place orders, and update order statuses. SQL Server stores customers, products, orders, and order items. The local HTTP API provides access to the product catalogue, customer order history, and checkout.

---

## ✨ Key Features

- 👤 **Customer Selection** — Choose a sample customer and access their order history.
- 📦 **Product Catalogue** — View electronics and clothing, including prices, discounts, and available stock.
- 🛒 **Shopping Cart** — Add and remove products, update quantities, and view the cart total in the console.
- 💰 **Product Discounts** — Calculate the final price using the rules for each product type.
- 🧾 **Checkout** — Save the order and its line items and update stock in one SQL Server transaction.
- 📋 **Order History** — Retrieve saved customer orders after restarting the application.
- 🚚 **Order Status Management** — Confirm, ship, deliver, or cancel orders according to the allowed status transitions.
- 🌐 **Local HTTP API** — List products, retrieve customer orders, and purchase one product type per checkout request.
- 🧪 **Software Testing** — Run JUnit tests with Maven and API checks with Postman. Manual checks cover checkout, stock updates, and order status persistence.

---

## 🧠 OOP Concepts Applied

| Concept | Description |
|---|---|
| **Encapsulation** | Keep product, cart, customer, and order data inside their respective classes. |
| **Inheritance** | Extend the abstract `Product` class with `Electronics` and `Clothing`. |
| **Abstraction** | Define common product behaviour in `Product`. |
| **Polymorphism** | Apply each product type’s own discount calculation. |
| **Enum** | Model the order lifecycle with `OrderStatus` and enforce valid state transitions in `Order`. |

---

## 🛠️ Technologies & Tools

<p align="center">
  <img src="https://img.shields.io/badge/IntelliJ_IDEA-IDE-000000?style=for-the-badge&logo=intellijidea&logoColor=white" alt="IntelliJ IDEA"/>
  <img src="https://img.shields.io/badge/JDBC-Database_Access-1F4E78?style=for-the-badge" alt="JDBC"/>
  <img src="https://img.shields.io/badge/Jackson-JSON-3776AB?style=for-the-badge" alt="Jackson"/>
  <img src="https://img.shields.io/badge/JUnit_5-Unit_Tests-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5"/>
  <img src="https://img.shields.io/badge/Postman-API_Testing-FF6C37?style=for-the-badge&logo=postman&logoColor=white" alt="Postman"/>
  <img src="https://img.shields.io/badge/GitHub-Repository-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/>
</p>

The project uses Java 21, Maven, Microsoft JDBC Driver for SQL Server, Java’s built-in `HttpServer`, and Jackson.

---

## 📂 Project Structure

```text
E-Commerce-System/
├── postman/
│   └── E-Commerce API.postman_collection.json
├── sql/
│   ├── 00-create-database.sql
│   ├── 01-products.sql
│   ├── 02-orders.sql
│   └── 03-customers.sql
├── src/
│   ├── main/java/org/example/
│   │   ├── Main.java
│   │   ├── ApiServer.java
│   │   ├── ProductRepository.java
│   │   ├── OrderRepository.java
│   │   ├── Product.java
│   │   ├── Electronics.java
│   │   ├── Clothing.java
│   │   ├── Cart.java
│   │   ├── CartItem.java
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   ├── OrderStatus.java
│   │   ├── EmptyCartException.java
│   │   ├── InsufficientStockException.java
│   │   └── ProductNotFoundException.java
│   └── test/java/org/example/
│       └── CartTest.java
├── pom.xml
└── README.md
```

---

## ▶️ How to Run

### Prerequisites

- Windows with 64-bit JDK 21 and IntelliJ IDEA
- SQL Server running locally with TCP/IP enabled on port `1433`
- SQL Server Management Studio (SSMS)
- Postman Desktop for API testing
- Maven, or IntelliJ IDEA’s bundled Maven

### 1. Set up SQL Server

In SSMS, connect to `tcp:localhost,1433` using **Windows Authentication**.
Run the scripts in the [`sql`](sql/) folder in this order:

1. [`00-create-database.sql`](sql/00-create-database.sql)
2. [`01-products.sql`](sql/01-products.sql)
3. [`02-orders.sql`](sql/02-orders.sql)
4. [`03-customers.sql`](sql/03-customers.sql)

The scripts create the `ECommerceSystem` database and its four tables:
`Products`, `Customers`, `Orders`, and `OrderItems`. They also add the
sample products and customers used by the application.

### 2. Configure Java and JDBC

1. Clone or download the repository and open it as a Maven project in IntelliJ IDEA.
2. Set the Project SDK to **JDK 21** and reload Maven.
3. Download [`mssql-jdbc_auth-13.6.2.x64.dll`](https://repo.maven.apache.org/maven2/com/microsoft/sqlserver/mssql-jdbc_auth/13.6.2.x64/mssql-jdbc_auth-13.6.2.x64.dll) and place it in `C:\sqljdbc-auth`.
4. Add the following VM option to the run configurations for both `Main` and `ApiServer`:

```text
-Djava.library.path=C:\sqljdbc-auth
```

[Microsoft JDBC documentation for Windows Authentication](https://learn.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url#connecting-with-integrated-authentication-on-windows)

### 3. Start the console application or API

- Run **`org.example.Main`** to use the console application. Select a customer by ID to open the menu.
- Run **`org.example.ApiServer`** to start the API at `http://127.0.0.1:8080`. Keep it running while sending requests from Postman.

---

## 🌐 API Endpoints

| Method | Endpoint | What it does | Success response |
|---|---|---|---|
| `GET` | `/products` | Returns the product list with original prices, discounted prices, and stock. | `200 OK` |
| `GET` | `/orders?customerId=1` | Returns the order history for the supplied customer ID. | `200 OK` |
| `POST` | `/checkout` | Creates an order for one product type and reduces its stock by the requested quantity. | `201 Created` |

The API binds to `127.0.0.1` for local demonstrations with sample customers.

---

## 🧪 Testing

### JUnit unit tests

Run this command from the project root:

```bash
mvn test
```

You can also use **Maven → Lifecycle → test** in IntelliJ IDEA. `CartTest` checks the discounted total for two laptops and verifies that adding a quantity above stock leaves the cart empty.

**Recorded result:** 2 tests passed, with 0 failures and 0 errors.

### Postman API tests

1. Start `ApiServer` and import the [E-Commerce API collection](postman/E-Commerce%20API.postman_collection.json) into Postman Desktop.
2. On a fresh database, create the order required by the history test. Send the request below **once, before creating any other order**. If order `1` already belongs to customer `1` and has a total of `54000`, skip this setup request.

```http
POST http://127.0.0.1:8080/checkout
Content-Type: application/json

{"customerId":1,"productId":1,"quantity":2}
```

3. Run **E-Commerce API → Run collection**. The collection checks product data and pricing, rejects `POST /products`, reads customer order history, rejects a missing customer ID, and rejects a checkout quantity of zero.

**Recorded result:** 5 requests, 10 passed assertions, 0 failures, and 0 errors.

The successful checkout was also verified manually in Postman. The new order appeared in the selected customer’s history, and the Laptop stock decreased by one.

[Postman Collection Runner documentation](https://learning.postman.com/docs/tests-and-scripts/running-collections/intro-to-collection-runs)

---

## 🎯 Learning Objectives

- Apply Java fundamentals and Object-Oriented Programming to a shopping workflow.
- Model customers, products, and orders in a relational database.
- Use JDBC and SQL transactions to keep orders and inventory consistent.
- Build a local HTTP API and return JSON responses.
- Write JUnit tests and verify successful and invalid API requests in Postman.
- Validate business rules for stock limits, checkout, and order status changes.

---

## 👥 Team Members

- Ahmed Gaber Abdeldayem Abdullah
- Amr Gamal Elsayed Abdelaziz
- Nada Yasser Mahmoud Mohammed
- Makarious Zaytoun Shawky Wahba
- Berta Emil Iskandar Sedhom