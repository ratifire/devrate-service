# Project: devRate

## Dependencies

### 1. Spring Boot Starter Web
- **Description:** Spring Boot Starter Web is a fundamental dependency for developing web applications using the Spring 
Framework. It includes everything needed to set up a basic web application, handling HTTP requests, and providing 
essential web-related functionalities.
- **Artifact ID:** spring-boot-starter-web
- **Group ID:** org.springframework.boot

### 2. Spring Boot Starter Data JPA
- **Description:** Spring Boot Starter Data JPA is a powerful dependency for simplifying the implementation of JPA-based 
repositories in Spring applications. It seamlessly integrates Spring Data JPA with Hibernate, providing easy and 
efficient data access through Java Persistence API (JPA).
- **Artifact ID:** spring-boot-starter-data-jpa
- **Group ID:** org.springframework.boot

### 3. Spring Boot Starter Test
- **Description:** Spring Boot Starter Test is a testing-centric dependency for Spring Boot applications. It includes 
essential testing frameworks like JUnit, Hamcrest, and Mockito, making it easy to write and execute tests for Spring Boot 
project.
- **Artifact ID:** spring-boot-starter-test
- **Group ID:** org.springframework.boot
- **Scope:** Test

### 4. PostgreSQL JDBC Driver
- **Description:** PostgreSQL JDBC Driver is an essential component for connecting your Java application to a PostgreSQL
database. It provides the necessary functionality to establish a connection, send queries, and retrieve results from the
PostgreSQL database.
- **Artifact ID:** postgresql
- **Group ID:** org.postgresql
- **Scope:** runtime
- **Version:** 42.7.1

### 5. Project Lombok
- **Description:** Lombok is a Java library that helps reduce boilerplate code by providing annotations to automatically 
generate common code constructs such as getters, setters, constructors, and more. It enhances code readability and 
conciseness.
- **Artifact ID:** lombok
- **Group ID:** org.projectlombok
- **Scope:** annotationProcessor
- **Links:** https://www.baeldung.com/intro-to-project-lombok

### 6. H2 Database
- **Description:** H2 is an open-source lightweight Java database. This database should be used exclusively for running
automated tests, ensuring that our tests are fast, reliable, and do not interfere with production or development
databases. Mainly, H2 database can be configured to run as in memory database, which means that data will not
persist on the disk. Because of embedded database it is not used for production development, but mostly used for
development and testing.
- **Artifact ID:** h2
- **Group ID:** com.h2database
- **Scope:** test
- **Links:** https://www.h2database.com/html/main.html : https://www.baeldung.com/spring-boot-h2-database

## Project Information

- **Group ID:** com.ratifire
- **Artifact ID:** devRate
- **Version:** 0.0.1-SNAPSHOT
- **Java Version:** 21