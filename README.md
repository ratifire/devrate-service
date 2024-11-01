# Project: devRate

## Dockerfile. Нow to build and run the container

**1. Build the Docker image:**
- Open a terminal.
- Enter: "docker build -t [enter tag-name for your Container] ."
 (For example: `docker build -t devrate-container .`)

**2. Running the Container:**
- docker run -p [free port local PC]:[free port in Container] [enter tag-name for your Container]
 (For example: `docker run -p 8087:8088 devrate-container`)

**3. Stopping the Container**
- Open a terminal.
- docker stop $(docker ps -a -q --filter ancestor=[enter tag-name for your Container])
 (For example: `docker stop $(docker ps -a -q --filter ancestor=devrate-container)`)

## Docker-compose. Нow to start the project
**Getting Started**
- Start the services using Docker Compose: `docker-compose up`
- To check the status and logs of the running containers, use the following command: `docker-compose ps`

**Stopping the Services**
- To stop the services and remove the containers, run the following command:`docker-compose down`

**Cleaning Up**
- To remove the data volume as well, you can use the following command:`docker-compose down -v`

## Docker-compose-dev. Нow to start the project in dev mode
- **Description:** This Docker Compose file is used by developers to simplify debugging and testing 
processes. It runs the db and mailhog services. The application should be run using the dev profile
and configured with environment variables such as database, user, and password.

**Getting Started**
- Start the services using Docker Compose Dev: `docker-compose -f docker-compose-dev.yml up`

## Profiles

### Available Profiles

1. Default Profile:
Common configuration.
2. Local Profile ('local'):
Configuration suitable for local development.
3. Development Profile ('dev'):
Additional configurations for the development environment.

### Switching Profiles

- To switch between profiles, you can use the spring.profiles.active property. 
For example, to run the application locally:
`spring.profiles.active=local`
- Or you can use maven command line:
`mvn spring-boot:run -Dspring-boot.run.profiles=dev`

### Local profile environment variables

1. PostgreSQL settings: 
- **PG_HOST** - Host. Default value: 'localhost'
- **PG_PORT** - Port number. Default value: '5432'
- **PG_DATABASE** - Database name. Default value: 'devrate'
- **PG_USERNAME** - User name. Default value: 'devrate'
- **PG_PASSWORD** - Password. Default value: 'devrate'
- **PG_SCHEMA** - Schema name. Default value: 'devrate'

### Dev profile environment variables

1. PostgreSQL settings: 
- **PG_HOST** - localhost
- **PG_PORT** - 5432
- **PG_DATABASE** - devrateDB
- **PG_USERNAME** - admin
- **PG_PASSWORD** - root
- **PG_SCHEMA** - Schema name

## MailHog
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

## New Users for Application Testing

The following accounts have been added for application testing:

1. **User 1**
    - Email: john.rate3@tutamail.com (use https://app.tuta.com/login)
    - Password: Dev1234!!

2. **User 2**
    - Email: dev.rate3@proton.me (use https://account.proton.me/login)
    - Password: Dev1234!

These emails and password are also used to log in to the email account.

The following Email addresses are using for users registration (https://proton.me/):

1. **New Email 1**
    - Email: dev.rate1@proton.me (use https://account.proton.me/login)
    - Password: Dev1234!

2. **New Email 2**
    - Email: dev.rate2@proton.me (use https://account.proton.me/login)
    - Password: Dev1234!


# Project Information

- **Java Version:** 21