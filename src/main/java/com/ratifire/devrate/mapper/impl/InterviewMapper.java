package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.InterviewDto;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between Interview and InterviewDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class InterviewMapper implements DataMapper<InterviewDto, Interview> {

  @Mapping(source = "interview.id", target = "id")
  @Mapping(source = "interview.startTime", target = "startTime")
  @Mapping(source = "interview.role", target = "role")
  @Mapping(source = "hostId", target = "hostId")
  @Mapping(source = "hostFirstName", target = "hostFirstName")
  @Mapping(source = "hostLastName", target = "hostLastName")
  @Mapping(source = "masteryLevel", target = "masteryLevel")
  @Mapping(source = "specializationName", target = "specializationName")
  @Mapping(target = "interview.userId", ignore = true)
  @Mapping(target = "interview.masteryId", ignore = true)
  @Mapping(target = "interview.interviewId", ignore = true)
  @Mapping(target = "interview.roomUrl", ignore = true)
  public abstract InterviewDto toDto(
      Interview interview,
      int masteryLevel,
      String specializationName,
      long hostId,
      String hostFirstName,
      String hostLastName
  );

}
