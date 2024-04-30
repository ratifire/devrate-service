package com.ratifire.devrate.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class AchievementDto {

  private long id;

  private String link;

  private String summary;

  private String description;

}
