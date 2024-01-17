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

### 8. Liquibase

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

## Project Information

- **Group ID:** com.ratifire
- **Artifact ID:** devRate
- **Version:** 0.0.1-SNAPSHOT
- **Java Version:** 21