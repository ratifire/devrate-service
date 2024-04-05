package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.WorkExperienceDto;
import com.ratifire.devrate.entity.WorkExperience;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;

/**
 * Interface for mapping WorkExperience Dto to WorkExperience entities.
 */
@Mapper(componentModel = "spring")
public abstract class WorkExperienceMapper implements
    DataMapper<WorkExperienceDto, WorkExperience> {

}
