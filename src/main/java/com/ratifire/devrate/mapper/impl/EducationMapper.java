package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.entity.Education;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class EducationMapper implements DataMapper<EducationDto, Education> {

  @Mapping(target = "userInfoId", ignore = true)
  public abstract Education toEntity(EducationDto educationDto);

  public abstract EducationDto toDto(Education education);

  public abstract List<EducationDto> toDto(List<Education> educations);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userInfoId", ignore = true)
  public abstract void updateEntity(EducationDto educationDto, @MappingTarget Education education);

}
