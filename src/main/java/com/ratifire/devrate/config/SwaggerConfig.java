package com.ratifire.devrate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining custom OpenAPI configuration.
 */
@Configuration
public class SwaggerConfig {

  private static final String API_TITLE = "DevRate Platform API";
  private static final String API_DESCRIPTION = "API endpoints for managing user registration, "
      + "interview processes, and candidate evaluations on the Devrate platform.";
  private static final String API_TERMS_OF_SERVICE = "DevRate";
  private static final String CONTACT_NAME = "DevRate";
  private static final String CONTACT_URL = "https://devrate.com";
  private static final String CONTACT_EMAIL = "devrate@gmail.com";
  private static final String LICENSE_NAME = "DevRate";
  private static final String LICENSE_URL = "DevRate";
  private static final String API_VERSION = "0.0.1-SNAPSHOT";
  private static final String LOCAL_SERVER_DESCRIPTION = "Local environment";
  private static final String LOCAL_SERVER_URL = "http://localhost:8080";
  private static final String PRODUCTION_SERVER_DESCRIPTION = "Production environment";
  private static final String PRODUCTION_SERVER_URL = "https://devrate.com";
  private static final String REGISTRATION_TAG_NAME = "Registration";
  private static final String REGISTRATION_TAG_DESCRIPTION = "Endpoint for user registration";

  /**
   * Generates and returns custom OpenAPI configuration.
   *
   * @return Custom OpenAPI configuration with defined title, description etc.
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title(API_TITLE)
            .description(API_DESCRIPTION)
            .termsOfService(API_TERMS_OF_SERVICE)
            .contact(new Contact()
                .name(CONTACT_NAME)
                .url(CONTACT_URL)
                .email(CONTACT_EMAIL))
            .license(new License()
                .name(LICENSE_NAME)
                .url(LICENSE_URL))
            .version(API_VERSION))
        .servers(List.of(
            new Server().description(LOCAL_SERVER_DESCRIPTION).url(LOCAL_SERVER_URL),
            new Server().description(PRODUCTION_SERVER_DESCRIPTION).url(PRODUCTION_SERVER_URL)))
        .tags(List.of(
            new Tag().name(REGISTRATION_TAG_NAME).description(REGISTRATION_TAG_DESCRIPTION)));
  }
}
