package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.UserInfoDto;
import com.ratifire.devrate.entity.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping UserPersonalInfoDto to UserPersonalInfo entities.
 */
@Mapper(componentModel = "spring")
public interface UserInfoMapper {

  UserInfo toEntity(UserInfoDto userInfoDto);

  UserInfoDto toDto(UserInfo userInfo);

  void updateEntityFromDto(UserInfoDto userInfoDto, @MappingTarget UserInfo userInfo);
}
