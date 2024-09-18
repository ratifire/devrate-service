package com.ratifire.devrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

/**
 * Data transfer object (DTO) representing achievement.
 */
@Getter
@Builder
@EqualsAndHashCode
public class AchievementDto {

  private long id;

  @URL
  private String link;

  @Size(max = 75)
  @NotBlank(message = "must not be null or empty")
  private String summary;

  @Size(max = 170)
  @NotBlank(message = "must not be null or empty")
  private String description;

}
