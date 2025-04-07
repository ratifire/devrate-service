package com.ratifire.devrate.configuration;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

/**
 * Configuration class for setting up the Amazon SQS (Simple Queue Service) client.
 */
@Configuration
@Profile("local")
public class SqsConfig {

  /**
   * Creates and configures an {@link SqsAsyncClient} bean for asynchronous communication
   * with Amazon SQS.
   *
   * @return an {@link SqsAsyncClient} configured with the specified endpoint, credentials, region.
   */
  @Bean
  public SqsAsyncClient sqsAsyncClient() {
    return SqsAsyncClient.builder()
        .endpointOverride(URI.create("http://elasticmq:9324"))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create("accessKey", "secretKey")
            )
        )
        .region(Region.US_EAST_1)
        .build();
  }
}
