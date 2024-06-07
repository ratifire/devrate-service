package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Mastery and MasteryDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class MasteryMapper implements DataMapper<MasteryDto, Mastery> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  public abstract Mastery toEntity(MasteryDto masteryDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "skills", ignore = true)
  public abstract Mastery updateEntity(MasteryDto masteryDto,
      @MappingTarget Mastery mastery);
}
