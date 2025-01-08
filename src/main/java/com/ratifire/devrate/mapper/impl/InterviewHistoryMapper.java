package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.InterviewHistoryDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.InterviewHistory;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between InterviewHistory and InterviewHistoryDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class InterviewHistoryMapper implements
    DataMapper<InterviewHistoryDto, InterviewHistory> {

  @Mapping(target = "id", ignore = true)
  public abstract InterviewHistory toEntity(InterviewHistoryDto interviewHistoryDto);

  @Mapping(source = "dateTime", target = "dateTime")
  public abstract InterviewHistoryDto toDto(InterviewHistory interviewHistory);

}
