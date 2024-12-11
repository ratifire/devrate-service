package com.ratifire.devrate.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.exception.JsonConverterException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class for converting instances to JSON strings.
 */
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
      throw new JsonConverterException(e.getMessage(), e);
    }
  }

  /**
   * Deserializes a JSON string to an object of the given type.
   *
   * @param json  the JSON string to deserialize
   * @param clazz the class type of the object
   * @return the deserialized object
   */
  public static <T> T deserialize(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new JsonConverterException(e.getMessage(), e);
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

  private JsonConverter() {
    throw new IllegalStateException("Utility class");
  }
}
