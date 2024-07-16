package com.ratifire.devrate.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for loading default soft skills and mastery levels from JSON files.
 */
@Configuration
public class SpecializationConfig {

  private static final Map<Integer, String> BY_LEVEL = new HashMap<>();

  /**
   * Loads the default soft skills from a JSON file.
   *
   * @return List of default soft skills.
   */
  @Bean
  public List<String> defaultSoftSkills() {
    return loadStringFromJson("/static/data/specialization/default-soft-skill-names.json");
  }

  /**
   * Loads the default mastery levels from a JSON file and populates a map with the levels.
   *
   * @return Map of masteryLevels where the key is the level number and the value is the levelName.
   */
  @Bean
  public Map<Integer, String> defaultMasteryLevels() {
    List<String> masteryLevels = loadStringFromJson(
        "/static/data/specialization/mastery-levels.json");
    createMasteryLevels(masteryLevels);
    return BY_LEVEL;
  }

  /**
   * Loads a list of strings from a JSON file.
   *
   * @param path the path to the JSON file.
   * @return List of strings loaded from the JSON file.
   * @throws ResourceNotFoundException if the JSON resource is not found or fails to load.
   */
  public List<String> loadStringFromJson(String path) {
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

  /**
   * Populates the BY_LEVEL map with mastery levels.
   *
   * @param masteryLevels List of mastery levels.
   */
  private void createMasteryLevels(List<String> masteryLevels) {
    for (int i = 0; i < masteryLevels.size(); i++) {
      BY_LEVEL.put(i + 1, masteryLevels.get(i));
    }
  }

  /**
   * Retrieves the level number for a given level name.
   *
   * @param level the level name.
   * @return the level number corresponding to the given level name, or 0 if not found.
   */
  public static int getLevel(String level) {
    if (level == null) {
      throw new IllegalArgumentException("Mastery level cannot be null.");
    }
    return BY_LEVEL.entrySet().stream()
        .filter(entry -> entry.getValue().equals(level))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(0);
  }

  /**
   * Retrieves the level name for a given level number.
   *
   * @param level the level number.
   * @return the level name corresponding to the given level number, or null if not found.
   */
  public static String getByLevel(int level) {
    return BY_LEVEL.get(level);
  }
}
