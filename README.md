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
- **Version:** 42.7.1

### 5. Project Lombok
- **Description:** Lombok is a Java library that helps reduce boilerplate code by providing annotations to automatically 
generate common code constructs such as getters, setters, constructors, and more. It enhances code readability and 
conciseness.
- **Artifact ID:** lombok
- **Group ID:** org.projectlombok
- **Version:** 1.18.30
- **Scope:** Provided

### 6. SpringDoc OpenApi
- **Description:** Springdoc-openapi java library helps to automate the generation of API documentation using 
spring boot projects.Springdoc-openapi works by examining an application at runtime to infer API semantics based 
on spring configurations, class structure and various annotations. Automatically generates documentation in JSON/YAML 
and HTML format APIs. This documentation can be completed by comments using swagger-api annotations.
- **Artifact ID:** springdoc-openapi-starter-webmvc-ui
- **Group ID:** org.springdoc
- **Version:** 2.3.0
- **URL:** https://springdoc.org
- **Getting Started:** The Swagger UI page is available at `http://server:port/swagger-ui/index.html and`
the OpenAPI description is available at the following url for json format: `http://server:port/v3/api-docs`
    - Server: The server name or IP
    - Port: The server port

## Project Information

- **Group ID:** com.ratifire
- **Artifact ID:** devRate
- **Version:** 0.0.1-SNAPSHOT
- **Java Version:** 21