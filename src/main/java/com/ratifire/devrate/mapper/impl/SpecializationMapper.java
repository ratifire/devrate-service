package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Specialization and SpecializationDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class SpecializationMapper implements
    DataMapper<SpecializationDto, Specialization> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mainMastery", ignore = true)
  @Mapping(target = "completedInterviews", ignore = true)
  @Mapping(target = "conductedInterviews", ignore = true)
  @Mapping(target = "masteries", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract Specialization toEntity(SpecializationDto specializationDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mainMastery", ignore = true)
  @Mapping(target = "completedInterviews", ignore = true)
  @Mapping(target = "conductedInterviews", ignore = true)
  @Mapping(target = "masteries", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract Specialization updateEntity(SpecializationDto specializationDto,
      @MappingTarget Specialization specialization);
}
