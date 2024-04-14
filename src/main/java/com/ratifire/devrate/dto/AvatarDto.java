package com.ratifire.devrate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Data Transfer Object (DTO) representing the avatar. */
@Builder
@Getter
@AllArgsConstructor
public class AvatarDto {
  private String avatarUrl;
}
