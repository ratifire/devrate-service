package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling OAuth authorization details.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OauthAuthorizationDto {

  @NotBlank(message = "code cannot be blank")
  private String authorizationCode;
  @NotBlank(message = "state cannot be blank")
  private String state;

}