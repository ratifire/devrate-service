package com.ratifire.devrate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main application class for DevRate.
 */
@SpringBootApplication
@EnableScheduling
public class DevRateApplication {

  public static void main(String[] args) {
    SpringApplication.run(DevRateApplication.class, args);
  }

}
