package com.ratifire.devrate.security.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OauthExchangeCodeRequest {

  @NotBlank(message = "code cannot be blank")
  private String code;
  @NotBlank(message = "state cannot be blank")
  private String state;

}