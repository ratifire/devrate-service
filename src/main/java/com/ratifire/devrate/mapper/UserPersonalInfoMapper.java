package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.UserPersonalInfoDto;
import com.ratifire.devrate.entity.UserPersonalInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping UserPersonalInfoDto to UserPersonalInfo entities.
 */
@Mapper(componentModel = "spring")
public interface UserPersonalInfoMapper {

  UserPersonalInfo toEntity(UserPersonalInfoDto userPersonalInfoDto);

  UserPersonalInfoDto toDto(UserPersonalInfo userPersonalInfo);

  void updateEntityFromDto(UserPersonalInfoDto dto, @MappingTarget UserPersonalInfo entity);
}
