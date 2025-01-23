package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.ParticipantDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface for mapping User entity to Participant Dto.
 */
@Mapper(componentModel = "spring")
public abstract class ParticipantMapper implements DataMapper<ParticipantDto, User> {

  @Mapping(source = "user.firstName", target = "name")
  @Mapping(source = "user.lastName", target = "surname")
  @Mapping(target = "role", source = "role")
  public abstract ParticipantDto toDto(User user, InterviewRequestRole role);

}
