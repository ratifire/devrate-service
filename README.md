# Project: devRate

## Dependencies

### Spring Boot Starter Web
- **Description:** Spring Boot Starter Web is a fundamental dependency for developing web applications 
using the Spring Framework. It includes everything needed to set up a basic web application, handling 
HTTP requests, and providing essential web-related functionalities.

### Spring Boot Starter Data JPA
- **Description:** Spring Boot Starter Data JPA is a powerful dependency for simplifying the 
implementation of JPA-based repositories in Spring applications. It seamlessly integrates Spring Data 
JPA with Hibernate, providing easy and efficient data access through Java Persistence API (JPA).

### Spring Boot Starter Validation
- **Description:** Spring Boot Starter Validation is a powerful component within the Spring Boot 
framework designed to streamline and enhance the validation process in your Java applications. 
Although Spring Boot Starter Validation integration with custom validators, the de-facto standard for 
performing validation is Hibernate Validator.
- **Links** https://www.baeldung.com/spring-boot-bean-validation

### Spring Boot Starter Test
- **Description:** Spring Boot Starter Test is a testing-centric dependency for Spring Boot applications. 
It includes essential testing frameworks like JUnit, Hamcrest, and Mockito, making it easy to write 
and execute tests for Spring Boot project.

### PostgreSQL JDBC Driver
- **Description:** PostgreSQL JDBC Driver is an essential component for connecting your Java application 
to a PostgreSQL database. It provides the necessary functionality to establish a connection, send queries, 
and retrieve results from the PostgreSQL database.

### Project Lombok
- **Description:** Lombok is a Java library that helps reduce boilerplate code by providing annotations 
to automatically generate common code constructs such as getters, setters, constructors, and more. It 
enhances code readability and conciseness.
- **Links:** https://www.baeldung.com/intro-to-project-lombok

### H2 Database
- **Description:** H2 is an open-source lightweight Java database. This database should be used exclusively 
for running automated tests, ensuring that our tests are fast, reliable, and do not interfere with 
production or development databases. Mainly, H2 database can be configured to run as in memory database, 
which means that data will not persist on the disk. Because of embedded database it is not used for 
production development, but mostly used for development and testing.
- **Links:** https://www.h2database.com/html/main.html : https://www.baeldung.com/spring-boot-h2-database

### SpringDoc OpenApi
- **Description:** Springdoc-openapi java library helps to automate the generation of API documentation 
using spring boot projects.Springdoc-openapi works by examining an application at runtime to infer 
API semantics based on spring configurations, class structure and various annotations. Automatically
generates documentation in JSON/YAML and HTML format APIs. This documentation can be completed by 
comments using swagger-api annotations.
- **URL:** https://springdoc.org
- **Getting Started:** The Swagger UI page is available at `http://server:port/swagger-ui/index.html and`
the OpenAPI description is available at the following url for json format: `http://server:port/v3/api-docs`
- Server: The server name or IP
- Port: The server port

### MapStruct
- **Description:** MapStruct is a Java-based code generation library that simplifies the process of 
mapping between Java bean types. It is particularly useful when working with complex domain models or 
when converting between different layers of an application, such as DTOs and entity classes.
- **Reference guide and usage guidelines:** https://mapstruct.org/documentation/reference-guide/

### Spring Boot Mail Starter
- **Description:** The Spring Boot Mail Starter enables easy integration of email functionalities.
It automatically configures the necessary beans for sending emails, simplifying the process of 
integrating email capabilities.
- **URL:** [Baeldung](https://www.baeldung.com/spring-email)

### Liquibase

- **Description:** [Liquibase](https://www.liquibase.org/) is an open-source database-independent library for tracking,
managing, and applying database schema changes. It provides a flexible and extensible framework for 
versioning your database schema and applying changes in a consistent and automated manner.

**<span style="color: lightseagreen">Configuration</span>**

1. **Configure Spring Boot Application Properties**

   Open your application.properties or application.yml file and add the Liquibase configuration:
    
   *spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.yaml*

2. **Create Liquibase Changelog File**

   Create a db/changelog directory in src/main/resources and add a db.changelog-master.yaml file:

You can now start adding your Liquibase change sets.

**<span style="color: lightseagreen">Best practices for managing database evolution</span>**

1. **Project Structure:**
   
    Create a well-organized project structure that includes directories for changelog files, 
configurations, and additional resources.

2. **One Changelog per Change Set:**

   Organize your changes into small, atomic change sets. Each change set should represent a single 
logical change to the database schema. This makes it easier to understand and manage changes.

3. **Use Clear and Descriptive ChangeSet IDs:**

   Choose meaningful IDs for your change sets to make it easier to identify them. 
Use a consistent naming convention that includes information about the change.

4. **Use Comment Tags:**

   Include comment tags in your changelog files to provide additional context for each change.
This can be helpful for understanding the purpose behind it.

5. **Documentation:**

   Document each changelog file and change set with details on the purpose of the changes, any special 
considerations, and potential rollback strategies.

### Spring Boot Actuator
- **Description:** Spring Boot Actuator provides production-ready features to help you monitor and 
manage your application. 
It exposes various endpoints for health checks, metrics, and other operational insights.
- **Artifact ID:** spring-boot-starter-actuator
- **Group ID:** org.springframework.boot
- **Links:** [Spring Boot Actuator Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html)

### Dockerfile. Нow to build and run the container
**1. Build the Docker image:**
- Open a terminal.
- Enter: "docker build -t [enter tag-name for your Container] ."
  (For example: "docker build -t devrate-container .")

**2. Running the Container:**
- docker run -p [free port local PC]:[free port in Container] [enter tag-name for your Container]
  (For example: "docker run -p 8087:8088 devrate-container")

**3. Stopping the Container**
- Open a terminal.
- docker stop $(docker ps -a -q --filter ancestor=[enter tag-name for your Container])
  (For example: "docker stop $(docker ps -a -q --filter ancestor=devrate-container)")

### Docker-compose. Нow to start the project
**Getting Started**
- Start the services using Docker Compose: `docker-compose up`
- To check the status and logs of the running containers, use the following command: `docker-compose ps`

**Stopping the Services**
- To stop the services and remove the containers, run the following command:`docker-compose down`

**Cleaning Up**
- To remove the data volume as well, you can use the following command:`docker-compose down -v`

### Docker-compose-dev. Нow to start the project in dev mode
- **Description:** This Docker Compose file is used by developers to simplify debugging and testing 
processes. It runs the db and mailhog services. The application should be run using the dev profile
and configured with environment variables such as database, user, and password.

**Getting Started**
- Start the services using Docker Compose Dev: `docker-compose -f docker-compose-dev.yml up`

### Profiles
- **Description:** Spring profiles are a feature in the Spring Framework that allows developers to 
define and group beans and configuration settings based on different runtime environments or application 
states. This enables the application to behave differently under different conditions, such as 
development, testing, production, etc. Spring profiles provide a way to manage configuration and bean 
definitions for specific scenarios.
- Profiles in this project help manage different configurations for various environments. 
By default, the project uses the 'default' and the 'local' profiles.

#### Available Profiles

1. Default Profile:
Common configuration.
2. Local Profile ('local'):
Configuration suitable for local development.
3. Development Profile ('dev'):
Additional configurations for the development environment.

#### Switching Profiles

- To switch between profiles, you can use the spring.profiles.active property. 
For example, to run the application locally:
spring.profiles.active=local
- Or you can use maven command line:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

#### Local profile environment variables

1. PostgreSQL settings: 
- **PG_HOST** - Host. Default value: 'localhost'
- **PG_PORT** - Port number. Default value: '5432'
- **PG_DATABASE** - Database name. Default value: 'devrate'
- **PG_USERNAME** - User name. Default value: 'devrate'
- **PG_PASSWORD** - Password. Default value: 'devrate'
- **PG_SCHEMA** - Schema name. Default value: 'devrate'

#### Dev profile environment variables

1. PostgreSQL settings: 
- **PG_HOST** - localhost
- **PG_PORT** - 5432
- **PG_DATABASE** - devrateDB
- **PG_USERNAME** - admin
- **PG_PASSWORD** - root
- **PG_SCHEMA** - Schema name

### Code style check
- **Description:** Checkstyle is a tool used to help ensure that Java code adheres to a coding standard. 
It automates the process of checking Java code, which is maintaining code quality and readability. 
Project uses checkstyle **Google Java Style** to enforce coding standards. The Google Java Style Guide 
to familiarize yourself with the rules can be found at the following 
link: https://google.github.io/styleguide/javaguide.html, https://checkstyle.sourceforge.io/index.html

### MailHog
- **Description:** MailHog is an email testing tool for developers that allows you to capture and 
view emails sent by your application in a web interface. It is an ideal tool for development environments, 
making it easy to test email notifications without sending them to actual email addresses.
- **URL:** [MailHog GitHub](https://github.com/mailhog/MailHog).
- **Getting Started:**
  - MailHog has been integrated into our Docker Compose setup to simplify email testing in development 
  environments. 
  This integration ensures that MailHog is automatically launched when you run the Docker Compose file.
  - URL for MailHog UI: http://localhost:8025
  - The MailHog web interface allows you to view and manage emails sent by your application. Simply 
  navigate to the URL in your web browser to access the MailHog UI.
  - MailHog is configured to be available only when using the local Spring profile.

### Spring Boot Starter Security

- **Description:** Spring Boot Starter Security, part of the Spring Boot framework, streamlines
  security for Java apps, integrating with Spring Security. It enables easy implementation of
  authentication, authorization, and other security measures.
  [Learn more](https://spring.io/projects/spring-security)

### Spring Security Test

- **Description:** Spring Security Test is a testing-centric dependency for Spring Security
  applications. It provides utilities and tools for testing Spring Security configurations,
  authentication mechanisms, and access control rules.
  [Learn more](https://docs.spring.io/spring-security/reference/servlet/test/index.html)

### New Users for Application Testing

The following accounts have been added for application testing:

1. **User 1**
    - Email: dev1@test.com
    - Password: 12345678

2. **User 2**
    - Email: dev2@test.com
    - Password: 12345678

3. **User 3**
    - Email: dev3@test.com
    - Password: 12345678


## Project Information

- **Group ID:** com.ratifire
- **Artifact ID:** devRate
- **Version:** 0.0.1-SNAPSHOT
- **Java Version:** 21