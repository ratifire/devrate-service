package com.ratifire.devrate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the basic information of a user used for search results.
 */
@Data
@AllArgsConstructor
public class UserNameSearchDto {
  private long id;
  private String firstName;
  private String lastName;
  private String status;
  private String picture;
}