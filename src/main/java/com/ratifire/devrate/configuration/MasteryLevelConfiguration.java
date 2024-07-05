package com.ratifire.devrate.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.entity.MasteryLevel;
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

  private static final Map<Integer, MasteryLevel> BY_LEVEL = new HashMap<>();

  /**
   * Loads the mastery levels from a JSON file.
   *
   * @return List of mastery levels as MasteryLevel.
   */
  @Bean
  public List<MasteryLevel> masteryLevels() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<MasteryLevel> masteryLevels = objectMapper.readValue(
        new ClassPathResource(
            "static/data/specialization/mastery-level-json.json").getInputStream(),
        new TypeReference<List<MasteryLevel>>() {
        }
    );

    for (MasteryLevel e : masteryLevels) {
      BY_LEVEL.put(e.getLevel(), e);
    }

    return masteryLevels;
  }

  public static MasteryLevel getByLevel(int level) {
    return BY_LEVEL.get(level);
  }
}
