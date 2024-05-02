package com.ratifire.devrate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Data Transfer Object (DTO) representing the picture. */
@Getter
@AllArgsConstructor
public class PictureDto {
  private byte[] picture;
}
