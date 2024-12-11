package com.ratifire.devrate.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for converting instances to JSON strings.
 */
@Slf4j
public class JsonConverter {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Serializes an object to a JSON string.
   *
   * @param object the object to serialize
   * @return the serialized JSON string
   */
  public static <T> String serialize(T object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize object to JSON", e);
    }
  }

  /**
   * Loads a list of strings from a JSON file located at the specified path.
   *
   * @param path the path to the JSON file
   * @return a {@link List} of strings deserialized from the JSON file
   * @throws ResourceNotFoundException if the file is not found or cannot be deserialized
   */
  public static List<String> loadStringFromJson(String path) {
    try (InputStream inputStream = JsonConverter.class.getResourceAsStream(path)) {
      return objectMapper.readValue(inputStream, new TypeReference<>() {});
    } catch (IOException e) {
      throw new ResourceNotFoundException("Failed to load data from: " + path);
    }
  }
}
