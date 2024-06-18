package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.enums.MasteryLevel;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper interface for mapping between Mastery and MasteryDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class MasteryMapper implements DataMapper<MasteryDto, Mastery> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "level", source = "masteryDto.level", qualifiedByName = "getLevel")
  public abstract Mastery toEntity(MasteryDto masteryDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "level", source = "masteryDto.level", qualifiedByName = "getLevel")
  public abstract Mastery updateEntity(MasteryDto masteryDto,
      @MappingTarget Mastery mastery);

  @Mapping(target = "level", source = "entity.level", qualifiedByName = "getMasteryLevel")
  public abstract MasteryDto toDto(Mastery entity);

  @Named("getLevel")
  public static int getLevel(MasteryLevel masteryLevel) {
    return masteryLevel.getLevel();
  }

  @Named("getMasteryLevel")
  public static MasteryLevel getMasteryLevel(int level) {
    return MasteryLevel.getByLevel(level);
  }
}
