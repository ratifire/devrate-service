package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.User;
import org.mapstruct.Mapper;

/**
 * Interface for mapping SignUpDto to User entities.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  User toEntity(SignUpDto signUpDto);
}
