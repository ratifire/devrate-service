package com.ratifire.devrate.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Configuration class for loading mastery levels from a JSON file.
 */
@Configuration
public class MasteryLevelConfiguration {

  private static final Map<Integer, String> BY_LEVEL = new HashMap<>();

  /**
   * Loads the mastery levels from a JSON file.
   *
   * @return Map of mastery levels as MasteryLevel.
   */
  @Bean
  public Map<Integer, String> masteryLevels() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> masteryLevels = objectMapper.readValue(
        new ClassPathResource(
            "static/data/specialization/mastery-level-json.json").getInputStream(),
        new TypeReference<List<String>>() {
        }
    );
    for (int i = 0; i < masteryLevels.size(); i++) {
      BY_LEVEL.put(i + 1, masteryLevels.get(i));
    }
    return BY_LEVEL;
  }

  public static int getLevel(String level) {
    int result = 0;
    for (int i = 0; i < BY_LEVEL.size(); i++) {
      if (BY_LEVEL.get(i).equals(level)) {
        result = i + 1;
      }
    }
    return result;
  }

  public static String getByLevel(int level) {
    return BY_LEVEL.get(level);
  }
}
