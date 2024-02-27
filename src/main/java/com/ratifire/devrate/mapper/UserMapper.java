package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface for mapping SignUpDto to User entities.
 */
@Mapper(componentModel = "spring", uses = PasswordEncoderMapper.class)
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "password", qualifiedByName = {"PasswordEncoderMapper", "encode"})
  User toEntity(SignUpDto signUpDto, Role role);

  SignUpDto toDto(User user);
}
