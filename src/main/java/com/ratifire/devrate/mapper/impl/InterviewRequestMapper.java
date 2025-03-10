package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.service.MasteryService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for converting between InterviewRequestDto and InterviewRequestV2 entities.
 */
@Mapper(componentModel = "spring", uses = {MasteryService.class})
public abstract class InterviewRequestMapper implements
    DataMapper<InterviewRequestDto, InterviewRequest> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mastery", source = "dto.masteryId",
      qualifiedByName = {"MasteryService", "getMasteryById"})
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "timeSlots", ignore = true)
  public abstract InterviewRequest toEntity(InterviewRequestDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mastery", source = "dto.masteryId",
      qualifiedByName = {"MasteryService", "getMasteryById"})
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "timeSlots", ignore = true)
  public abstract InterviewRequest updateEntity(InterviewRequestDto dto,
      @MappingTarget InterviewRequest entity);

  @Mapping(target = "timeSlots", ignore = true)
  public abstract InterviewRequestDto toDto(InterviewRequest request);
}
