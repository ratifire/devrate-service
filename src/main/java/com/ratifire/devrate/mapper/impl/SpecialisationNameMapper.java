package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.SpecialisationNameDto;
import com.ratifire.devrate.entity.SpecialisationName;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;


/**
 * Interface for mapping SpecialisationName Dto to Specialisation entities.
 */
@Mapper(componentModel = "spring")
public abstract class SpecialisationNameMapper implements
    DataMapper<SpecialisationNameDto, SpecialisationName> {

}