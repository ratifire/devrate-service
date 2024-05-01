package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.entity.Achievement;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Achievement and AchievementDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class AchievementMapper implements DataMapper<AchievementDto, Achievement> {

  public abstract AchievementDto toDto(Achievement achievement);

  @Mapping(target = "id", ignore = true)
  public abstract Achievement toEntity(AchievementDto achievementDto);

  @Mapping(target = "id", ignore = true)
  public abstract Achievement updateEntity(AchievementDto achievementDto,
      @MappingTarget Achievement achievement);
}
