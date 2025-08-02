# 🗂️ Taskboard in Java (Spring Boot + Maven)

<a href="https://github.com/Caroline-Teixeira/taskboard/blob/main/README.md">
<img src="https://raw.githubusercontent.com/yammadev/flag-icons/refs/heads/master/png/BR%402x.png" alt="Portuguese" ></a>

![Made with Java](https://img.shields.io/badge/Made%20with-Java-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-green?style=for-the-badge&logo=spring)
![Maven](https://img.shields.io/badge/Maven-Build%20Tool-important?style=for-the-badge&logo=apachemaven)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql)

## 📖 Description

This **Taskboard** is a task management application developed in **Java 21** with **Spring Boot**, using **Maven** for dependency management and **MySQL 8** as the database. The application allows creating boards, managing columns, adding, moving, blocking, unblocking, and deleting cards, as well as listing movements and block history. The interface is console-based, with interactive menus to facilitate user interaction.

The project follows a layered architecture (Model, Repository, Service, View) and uses **Spring Data JPA** for persistence, with support for transactions and robust validations.

## 🎯 Features

- ✅ Create and delete boards 
- ✅ Add and delete cards
- ✅ Move cards between columns 
- ✅ Block and unblock cards
- ✅ List columns of a board
- ✅ List card movements
- ✅ List block history and active blocks
- ✅ Rule validation (e.g.: don't delete blocked cards)
- ✅ Data persistence with MySQL 8.0
- ✅ Console interface with interactive menus

## 📂 Project Structure

```
.
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/board/taskboard/
│   │   │       ├── config/
│   │   │       │   └── AppConfig.java
│   │   │       ├── dto/
│   │   │       │   ├── BlockHistoryDTO.java
│   │   │       │   ├── BoardDTO.java
│   │   │       │   ├── CardDTO.java
│   │   │       │   ├── CardMovementDTO.java
│   │   │       │   └── TaskStatusDTO.java
│   │   │       ├── exception/
│   │   │       │   └── TaskboardException.java
│   │   │       ├── model/
│   │   │       │   ├── BlockHistory.java
│   │   │       │   ├── Board.java
│   │   │       │   ├── Card.java
│   │   │       │   ├── CardMovement.java
│   │   │       │   ├── Status.java
│   │   │       │   └── TaskStatus.java
│   │   │       ├── repository/
│   │   │       │   ├── BlockHistoryRepository.java
│   │   │       │   ├── BoardRepository.java
│   │   │       │   ├── CardMovementRepository.java
│   │   │       │   ├── CardRepository.java
│   │   │       │   └── TaskStatusRepository.java
│   │   │       ├── service/
│   │   │       │   ├── BlockHistoryService.java
│   │   │       │   ├── BoardService.java
│   │   │       │   ├── CardMovementService.java
│   │   │       │   ├── CardService.java
│   │   │       │   └── TaskStatusService.java
│   │   │       ├── util/
│   │   │       │   ├── AnsiColors.java
│   │   │       │   ├── ConsolePrinter.java
│   │   │       │   └── DateUtil.java
│   │   │       ├── view/
│   │   │       │   ├── BoardMenu.java
│   │   │       │   ├── CardActionsMenu.java
│   │   │       │   ├── CardMenu.java
│   │   │       │   ├── ListOptionsMenu.java
│   │   │       │   └── MainMenu.java
│   │   │       └── TaskboardApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── br/com/board/taskboard/
│               ├── service/
│               │   ├── BlockHistoryServiceTest.java
│               │   ├── BoardServiceTest.java
│               │   ├── CardMovementServiceTest.java
│               │   ├── CardServiceTest.java
│               │   └── TaskStatusServiceTest.java
│               └── TaskboardApplicationTests.java
└── target/
    └── classes/
        └── [compiled files]
```

## 🚀 How to Run

### 🖥️ Via IDE
1. Import the project as a Maven project in your IDE (e.g.: IntelliJ IDEA, Eclipse).
2. Configure the MySQL database according to the **Configuration** section.
3. Run the `TaskboardApplication.java` class as a Spring Boot application.

### 🖥️ Via Terminal
1. Follow the steps in the **Configuration** section to clone the repository and configure the database.
2. Compile and run:
```bash
mvn clean install
mvn spring-boot:run
```

### Prerequisites:
- **Java 21** or higher installed
- **Maven 3.9.6** installed and configured in PATH
- **MySQL 8.0** installed and configured

## 🛠️ Configuration

### Clone the repository:
```bash
git clone https://github.com/[your-username]/[your-repository].git
```

### Navigate to the directory:
```bash
cd [your-repository]
```

### Configure the database (MySQL):
```sql
CREATE DATABASE taskboard;
```

### Update the `application.properties` file:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taskboard
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Compile and run:
```bash
mvn clean install
mvn spring-boot:run
```

## 🛠️ Technologies Used

- **Java 21**
- **Spring Boot 3.x**
- **Maven 3.9.x**
- **MySQL 8** 
- **Spring Data JPA** 
- **Jakarta Transactions** for transaction management
- **Streams API** for collection manipulation

## 📖 Explanation of Main Classes and Packages

**Packages**
- `config` → Application configurations, in this case the Scanner.
- `dto` → Data Transfer Objects (DTOs) for communication between layers.
- `exception` → Custom exceptions, such as `TaskboardException`.
- `model` → JPA entities representing database tables (Board, Card, TaskStatus, etc.).
- `repository` → JPA interfaces for database operations.
- `service` → Business logic for manipulating boards, cards, and columns.
- `util` → Utility classes for console formatting, colors, and date manipulation.
- `view` → Console-based user interface classes with interactive menus.

**Main Classes**
- `TaskboardApplication` → Spring Boot application entry point.
- `BoardService` → Manages board creation, listing, and deletion.
- `CardService` → Manages card creation, deletion, and manipulation.
- `TaskStatusService` → Manages columns (TaskStatus) associated with boards.
- `CardMovementService` → Records card movements between columns.
- `BlockHistoryService` → Manages card block history.
- `MainMenu` → Main console interface with options for all functionalities.
- `ConsolePrinter` → Utility for formatted console printing.
- `DateUtil` → Utility for date manipulation.

## 📌 Tips for Improvement or Expansion

- 🖥️ Implement a graphical interface (e.g.: JavaFX or Swing) instead of console.
- 📊 Add reports or task statistics (e.g.: cards by status).
- 🔒 Implement user authentication for private boards.
- 🧪 Expand unit tests with JUnit to cover more cases.
- 💾 Support other databases (e.g.: PostgreSQL).
- 📱 Create a REST API for integration with web or mobile frontends.

## 📄 License

This project is under the [MIT](LICENSE) license.

## 👨‍💻 Author

<a href="https://github.com/Caroline-Teixeira">Caroline 💙</a>

---

📌 *Project developed for the DIO (Digital Innovation One) challenge.*