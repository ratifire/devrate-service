package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.LanguageDto;
import com.ratifire.devrate.entity.Language;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;

/**
 * Mapper abstract class for mapping between Language and LanguageDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class LanguageMapper implements DataMapper<LanguageDto, Language> {

}
