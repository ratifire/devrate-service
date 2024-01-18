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
- **Scope:** provided
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

### 7. SpringDoc OpenApi
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

### 7. MapStruct
- **Description:** MapStruct is a Java-based code generation library that simplifies the process of mapping between 
Java bean types. It is particularly useful when working with complex domain models or when converting between 
different layers of an application, such as DTOs and entity classes.
- **Artifact ID:** mapstruct
- **Group ID:** org.mapstruct
- **Version:** 1.5.5.Final
- **Scope:** Provided
- **Reference guide and usage guidelines:** https://mapstruct.org/documentation/reference-guide/

### Liquibase

- **Description:** [Liquibase](https://www.liquibase.org/) is an open-source database-independent library for tracking,
- managing, and applying database schema changes. It provides a flexible and extensible framework for versioning your
- database schema and applying changes in a consistent and automated manner.


- **Artifact ID:** liquibase-core
- **Group ID:** org.liquibase

**<span style="color: lightseagreen">Configuration</span>**

1. **Configure Spring Boot Application Properties**

   Open your application.properties or application.yml file and add the Liquibase configuration:
    
   *spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.yaml*

2. **Create Liquibase Changelog File**

   Create a db/changelog directory in src/main/resources and add a db.changelog-master.yaml file:

You can now start adding your Liquibase change sets.

**<span style="color: lightseagreen">Best practices for managing database evolution</span>**

1. **Project Structure:**
   
    Create a well-organized project structure that includes directories for changelog files, configurations, 
and additional resources.

2. **One Changelog per Change Set:**

   Organize your changes into small, atomic change sets. Each change set should represent a single logical change 
to the database schema. This makes it easier to understand and manage changes.

3. **Use Clear and Descriptive ChangeSet IDs:**

   Choose meaningful IDs for your change sets to make it easier to identify them. 
Use a consistent naming convention that includes information about the change.

4. **Use Comment Tags:**

   Include comment tags in your changelog files to provide additional context for each change.
This can be helpful for understanding the purpose behind it.

5. **Documentation:**

   Document each changelog file and change set with details on the purpose of the changes, any special considerations, 
and potential rollback strategies.

### Spring Boot Actuator
- **Description:** Spring Boot Actuator provides production-ready features to help you monitor and manage your application. 
It exposes various endpoints for health checks, metrics, and other operational insights.
- **Artifact ID:** spring-boot-starter-actuator
- **Group ID:** org.springframework.boot
- **Links:** [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html)

## Project Information

- **Group ID:** com.ratifire
- **Artifact ID:** devRate
- **Version:** 0.0.1-SNAPSHOT
- **Java Version:** 21