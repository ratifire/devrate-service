package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.UserMainMasterySkillDto;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between Specialization and UserMainMasterySkillDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class UserMainMasterySkillMapper implements
    DataMapper<UserMainMasterySkillDto, Specialization> {

  @Mapping(target = "specialization", source = "specialization")
  @Mapping(target = "mainMastery", source = "specialization.mainMastery")
  @Mapping(target = "mainMasterySkills", source = "specialization.mainMastery.skills")
  public abstract UserMainMasterySkillDto toDto(Specialization specialization);
}
