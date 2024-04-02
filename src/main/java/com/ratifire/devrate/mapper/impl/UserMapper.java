package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface for mapping UserDto to UserSecurity entities.
 */
@Mapper(componentModel = "spring", uses = PasswordEncoderMapper.class)
public abstract class UserMapper implements DataMapper<UserDto, UserSecurity> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "password", qualifiedByName = {"PasswordEncoderMapper", "encode"})
  public abstract UserSecurity toEntity(UserDto userDto);
}
