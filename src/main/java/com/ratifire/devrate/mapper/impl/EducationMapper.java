package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Education and EducationDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class EducationMapper implements DataMapper<EducationDto, Education> {

  public abstract EducationDto toDto(Education education);

  @Mapping(target = "id", ignore = true)
  public abstract Education toEntity(EducationDto educationDto);

  @Mapping(target = "id", ignore = true)
  public abstract Education updateEntity(EducationDto educationDto,
      @MappingTarget Education education);
}
