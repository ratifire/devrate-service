package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Education and EducationDto objects.
 */
@Mapper(componentModel = "spring")
public interface EducationMapper {

  @Mapping(target = "userInfoId", ignore = true)
  Education toEntity(EducationDto educationDto);

  EducationDto toDto(Education education);

  List<EducationDto> toDto(List<Education> educations);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userInfoId", ignore = true)
  void toUpdate(EducationDto educationDto, @MappingTarget Education education);

}
