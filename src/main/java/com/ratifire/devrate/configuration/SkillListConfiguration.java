package com.ratifire.devrate.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for loading default soft skill names from a JSON file.
 */
@Configuration
public class SkillListConfiguration {

  /**
   * Loads the default soft skills from a JSON file.
   *
   * @return List of skill names as Strings.
   */
  @Bean
  public List<String> loadDefaultSkills() {
    String path = "/static/data/specialization/deafult-soft-skill-names.json";
    ObjectMapper objectMapper = new ObjectMapper();
    try (InputStream inputStream = getClass().getResourceAsStream(path)) {
      if (inputStream == null) {
        throw new ResourceNotFoundException("Json resource not found.");
      }
      return objectMapper.readValue(inputStream, new TypeReference<List<String>>() {
      });
    } catch (IOException e) {
      throw new ResourceNotFoundException("Failed to load default skills");
    }
  }
}
