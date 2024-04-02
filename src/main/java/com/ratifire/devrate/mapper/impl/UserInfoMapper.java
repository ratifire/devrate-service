package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;

/**
 * Interface for mapping UserDto to User entities.
 */
@Mapper(componentModel = "spring")
public abstract class UserInfoMapper implements DataMapper<UserInfoDto, User> {

}
