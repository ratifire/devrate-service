package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.ParticipantDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.EventType;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.Collections;
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

  @Mapping(source = "user.firstName", target = "name")
  @Mapping(source = "user.lastName", target = "surname")
  @Mapping(source = "requestMastery.specialization.name", target = "status")
  @Mapping(source = "requestMastery.level", target = "level")
  @Mapping(source = "role", target = "role")
  @Mapping(source = "user.id", target = "id")
  public abstract ParticipantDto toDto(User user, InterviewRequestRole role,
      Mastery requestMastery);

  /**
   * Maps a list of User entities to ParticipantDto objects with a specified role.
   *
   * @param participants the list of User entities
   * @param role         the role to assign to each ParticipantDto
   * @return a list of ParticipantDto objects
   */
  public List<ParticipantDto> mapParticipants(List<User> participants, InterviewRequestRole role,
      Mastery requestMastery, EventType eventType) {
    if (eventType == EventType.INTERVIEW) {
      return participants.stream()
          .findFirst()
          .map(first -> List.of(toDto(first, role, requestMastery)))
          .orElse(Collections.emptyList());
    }
    return participants.stream()
        .map(user -> toDto(user, role))
        .toList();
  }
}
