package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.NicheDto;
import com.ratifire.devrate.entity.Niche;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping niche Dto to niche entities.
 */
@Mapper(componentModel = "spring")
public abstract class NicheMapper implements
    DataMapper<NicheDto, Niche> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "level", ignore = true)
  public abstract Niche toEntity(NicheDto nicheDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "level", ignore = true)
  public abstract Niche updateEntity(NicheDto nicheDto,
      @MappingTarget Niche niche);

}
