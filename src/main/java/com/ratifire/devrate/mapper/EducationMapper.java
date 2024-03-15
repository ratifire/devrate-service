package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Education and EducationDto objects.
 */
@Mapper(componentModel = "spring")
public interface EducationMapper {

  Education toEntity(EducationDto educationDto);

  EducationDto toDto(Education education);

  @Mapping(target = "id", ignore = true)
  void toUpdate(EducationDto educationDto, @MappingTarget Education education);

}
