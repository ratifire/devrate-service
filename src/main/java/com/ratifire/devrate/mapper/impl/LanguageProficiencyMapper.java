package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.entity.LanguageProficiency;
import com.ratifire.devrate.mapper.DataMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper abstract class for mapping between LanguageProficiency and LanguageProficiencyDto
 * objects.
 */
@Mapper(componentModel = "spring")
public abstract class LanguageProficiencyMapper implements
    DataMapper<LanguageProficiencyDto, LanguageProficiency> {

  @Mapping(target = "id", ignore = true)
  public abstract LanguageProficiency toEntity(LanguageProficiencyDto languageProficiencyDto);

  @Mapping(target = "id", ignore = true)
  public abstract LanguageProficiency updateEntity(LanguageProficiencyDto languageProficiencyDto,
      @MappingTarget LanguageProficiency languageProficiency);

}