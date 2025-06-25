package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.InterviewRequestTimeSlotDto;
import com.ratifire.devrate.entity.interview.InterviewRequestTimeSlot;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between InterviewRequestTimeSlot and InterviewRequestTimeSlotDto.
 */
@Mapper(componentModel = "spring")
public abstract class InterviewRequestTimeSlotMapper implements
    DataMapper<InterviewRequestTimeSlotDto, InterviewRequestTimeSlot> {

  @Mapping(target = "interviewRequest", ignore = true)
  public abstract List<InterviewRequestTimeSlotDto> toDto(List<InterviewRequestTimeSlot> entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "interviewRequest", ignore = true)
  public abstract List<InterviewRequestTimeSlot> toEntity(List<InterviewRequestTimeSlotDto> dto);
}