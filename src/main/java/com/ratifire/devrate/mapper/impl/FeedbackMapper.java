package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.FeedbackDto;
import com.ratifire.devrate.entity.Feedback;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for mapping between Feedback and FeedbackDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class FeedbackMapper implements DataMapper<FeedbackDto, Feedback> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  public abstract Feedback toEntity(FeedbackDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  public abstract Feedback updateEntity(FeedbackDto dto, @MappingTarget Feedback entity);
}
