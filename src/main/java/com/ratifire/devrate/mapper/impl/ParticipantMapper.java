package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.ParticipantDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface for mapping User entity to Participant Dto.
 */
@Mapper(componentModel = "spring")
public abstract class ParticipantMapper implements DataMapper<ParticipantDto, User> {


  @Mapping(source = "user.firstName", target = "name")
  @Mapping(source = "user.lastName", target = "surname")
  @Mapping(source = "user.status", target = "status")
  @Mapping(target = "role", source = "role")
  public abstract ParticipantDto toDto(User user, InterviewRequestRole role);

  /**
   * Maps a list of User entities to ParticipantDto objects with a specified role.
   *
   * @param participants the list of User entities
   * @param role         the role to assign to each ParticipantDto
   * @return a list of ParticipantDto objects
   */
  public List<ParticipantDto> mapParticipants(List<User> participants, InterviewRequestRole role) {
    return participants.stream()
        .map(user -> toDto(user, role))
        .toList();
  }
}
