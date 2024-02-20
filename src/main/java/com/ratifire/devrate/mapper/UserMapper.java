package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface for mapping SignUpDto to User entities.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  User toEntity(SignUpDto signUpDto);
}
