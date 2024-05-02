package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.dto.SpecialisationDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.Specialisation;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Interface for mapping Specialisation Dto to Specialisation entities.
 */
@Mapper(componentModel = "spring")
public abstract class SpecialisationMapper implements
    DataMapper<SpecialisationDto, Specialisation> {

}
