package com.ratifire.devrate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing the basic information of a user used for search results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserNameSearchDto {
  private long id;
  private String firstName;
  private String lastName;
}