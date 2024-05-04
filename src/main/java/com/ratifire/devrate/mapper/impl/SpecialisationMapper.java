package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.SpecialisationDto;
import com.ratifire.devrate.entity.Specialisation;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping specialisation Dto to specialisation entities.
 */
@Mapper(componentModel = "spring")
public abstract class SpecialisationMapper implements
    DataMapper<SpecialisationDto, Specialisation> {

  @Mapping(target = "id", ignore = true)
  public abstract Specialisation toEntity(SpecialisationDto specialisationDto);

  @Mapping(target = "id", ignore = true)
  public abstract Specialisation updateEntity(SpecialisationDto specialisationDto,
      @MappingTarget Specialisation specialisation);

}
