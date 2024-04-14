package com.ratifire.devrate.configuration;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Configures Google Cloud Storage service for Spring applications. */
@Configuration
@Profile("default")
public class StorageConfig {

  /**
   * Provides the Google Cloud Storage service instance.
   *
   * @return Configured Storage service instance.
   */
  @Bean
  public Storage getStorage() {
    return StorageOptions.getDefaultInstance().getService();
  }
}
