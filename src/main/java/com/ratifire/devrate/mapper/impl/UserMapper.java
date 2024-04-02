package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;

/**
 * Interface for mapping UserDto to User entities.
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper implements DataMapper<UserDto, User> {

}
