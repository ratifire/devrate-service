package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.NicheLevelDto;
import com.ratifire.devrate.entity.NicheLevel;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping niche level Dto to niche level entities.
 */
@Mapper(componentModel = "spring")
public abstract class NicheLevelMapper implements
    DataMapper<NicheLevelDto, NicheLevel> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "softSkill", ignore = true)
  @Mapping(target = "hardSkill", ignore = true)
  public abstract NicheLevel toEntity(NicheLevelDto nicheLevelDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "softSkill", ignore = true)
  @Mapping(target = "hardSkill", ignore = true)
  public abstract NicheLevel updateEntity(NicheLevelDto nicheLevelDto,
      @MappingTarget NicheLevel nicheLevel);
}
