package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.EventDto.ParticipantDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface for mapping Event Dto to Event entities.
 */
@Mapper(componentModel = "spring")
public abstract class ParticipantMapper implements DataMapper<ParticipantDto, User> {


  @Mapping(source = "user.firstName", target = "name")
  @Mapping(source = "user.lastName", target = "surname")
  @Mapping(source = "user.status", target = "status")
  @Mapping(target = "role", source = "role")
  public abstract ParticipantDto toDto(User user, InterviewRequestRole role);

  /**
   * Interface for mapping Event Dto to Event entities.
   */
  public List<ParticipantDto> mapParticipants(List<User> participants, InterviewRequestRole role) {
    return participants.stream()
        .map(user -> toDto(user, role))
        .toList();
  }

}
