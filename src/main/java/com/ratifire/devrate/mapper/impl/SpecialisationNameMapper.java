package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.SpecialisationDto;
import com.ratifire.devrate.entity.Specialisation;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;


/**
 * Interface for mapping SpecialisationName Dto to Specialisation entities.
 */
@Mapper(componentModel = "spring")
public abstract class SpecialisationNameMapper implements
    DataMapper<SpecialisationDto, Specialisation> {

}