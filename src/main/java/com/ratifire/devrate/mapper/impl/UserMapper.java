package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping UserDto to User entities.
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper implements DataMapper<UserDto, User> {

  @Mapping(target = "contacts", ignore = true)
  @Mapping(target = "educations", ignore = true)
  @Mapping(target = "notifications", ignore = true)
  @Mapping(target = "employmentRecords", ignore = true)
  @Mapping(target = "languageProficiencies", ignore = true)
  public abstract User toEntity(UserDto dto);

  @Mapping(target = "contacts", ignore = true)
  @Mapping(target = "educations", ignore = true)
  @Mapping(target = "notifications", ignore = true)
  @Mapping(target = "employmentRecords", ignore = true)
  @Mapping(target = "languageProficiencies", ignore = true)
  public abstract User updateEntity(UserDto dto, @MappingTarget User entity);
}
