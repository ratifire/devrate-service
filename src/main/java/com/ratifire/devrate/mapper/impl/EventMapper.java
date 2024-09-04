package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Interface for mapping Event entities to Event Dto.
 */
@Mapper(componentModel = "spring")
public abstract class EventMapper implements DataMapper<EventDto, Event> {

  protected ParticipantMapper participantMapper;

  @Autowired
  protected void setParticipantMapper(ParticipantMapper participantMapper) {
    this.participantMapper = participantMapper;
  }

  @Override
  @Mapping(source = "event.id", target = "id")
  @Mapping(source = "event.eventTypeId", target = "eventTypeId")
  @Mapping(source = "event.type", target = "type")
  @Mapping(source = "event.roomLink", target = "link")
  @Mapping(source = "event.startTime", target = "startTime")
  @Mapping(target = "host", expression = "java(participantMapper.toDto(host, "
      + "com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER))")
  @Mapping(target = "participantDtos", expression = "java(participantMapper.mapParticipants"
      + "(participants, com.ratifire.devrate.enums.InterviewRequestRole.CANDIDATE))")
  public abstract EventDto toDto(Event event, User host, List<User> participants);
}
