package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.entity.interview.InterviewRequestV2;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.service.MasteryService;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper for converting between InterviewRequestDto and InterviewRequestV2 entities.
 */
@Mapper(componentModel = "spring", uses = {MasteryService.class})
public abstract class InterviewRequestMapperV2 implements
    DataMapper<InterviewRequestDto, InterviewRequestV2> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mastery", source = "dto.masteryId",
      qualifiedByName = {"MasteryService", "getMasteryById"})
  @Mapping(target = "expiredAt", source = "dto.availableDates",
      qualifiedByName = "expiredAtFromAvailableDates")
  @Mapping(target = "user", ignore = true)
  public abstract InterviewRequestV2 toEntity(InterviewRequestDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mastery", source = "dto.masteryId",
      qualifiedByName = {"MasteryService", "getMasteryById"})
  @Mapping(target = "expiredAt", source = "dto.availableDates",
      qualifiedByName = "expiredAtFromAvailableDates")
  @Mapping(target = "user", ignore = true)
  public abstract InterviewRequestV2 updateEntity(InterviewRequestDto dto,
      @MappingTarget InterviewRequestV2 entity);

  @Mapping(target = "masteryId", source = "entity.mastery.id")
  public abstract InterviewRequestDto toDto(InterviewRequestV2 entity);

  /**
   * Returns the latest date from the given list of dates.
   *
   * @param availableDates a list of ZonedDateTime objects
   * @return the latest ZonedDateTime from the list
   */
  @Named("expiredAtFromAvailableDates")
  public static ZonedDateTime expiredAtFromAvailableDates(List<ZonedDateTime> availableDates) {
    return Collections.max(availableDates);
  }
}
