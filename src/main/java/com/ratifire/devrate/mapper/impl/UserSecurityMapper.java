package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.UserRegistrationDto;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping {@link UserRegistrationDto} to {@link UserSecurity} entities.
 */
@Mapper(componentModel = "spring", uses = PasswordEncoderMapper.class)
public abstract class UserSecurityMapper implements DataMapper<UserRegistrationDto, UserSecurity> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "password", qualifiedByName = {"PasswordEncoderMapper", "encode"})
  public abstract UserSecurity toEntity(UserRegistrationDto userRegistrationDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "password", qualifiedByName = {"PasswordEncoderMapper", "encode"})
  @Mapping(target = "authorities", ignore = true)
  public abstract UserSecurity updateEntity(UserRegistrationDto dto,
      @MappingTarget UserSecurity entity);

  @Mapping(target = "firstName", ignore = true)
  @Mapping(target = "lastName", ignore = true)
  @Mapping(target = "country", ignore = true)
  @Mapping(target = "subscribed", ignore = true)
  @Mapping(target = "password", ignore = true)
  public abstract UserRegistrationDto toDto(UserSecurity entity);
}
