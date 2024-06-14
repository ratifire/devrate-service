package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.InterviewSummaryDto;
import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between InterviewSummary and InterviewSummaryDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class InterviewSummaryMapper implements
    DataMapper<InterviewSummaryDto, InterviewSummary> {

  @Mapping(target = "id", ignore = true)
  public abstract InterviewSummary toEntity(InterviewSummaryDto interviewSummaryDto);

}
