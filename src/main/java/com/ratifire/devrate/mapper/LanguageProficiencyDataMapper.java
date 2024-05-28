package com.ratifire.devrate.mapper;

import com.ratifire.devrate.dto.LanguageProficiencyDataDto;
import com.ratifire.devrate.enums.LanguageProficiencyLevel;
import com.ratifire.devrate.enums.LanguageProficiencyName;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting LanguageProficiencyName enum values to LanguageProficiencyDataDto
 * Data Transfer Objects (DTOs).
 */
@Mapper(componentModel = "spring")
public interface LanguageProficiencyDataMapper {

  @Mapping(target = "levels", expression = "java(mapLevelsByLanguage(languageProficiencyName))")
  LanguageProficiencyDataDto toDto(LanguageProficiencyName languageProficiencyName);

  List<LanguageProficiencyDataDto> toDto(List<LanguageProficiencyName> languageProficiencyNames);

  default List<String> mapLevelsByLanguage(LanguageProficiencyName languageProficiencyName) {
    return LanguageProficiencyLevel.getLevelsByLanguage(languageProficiencyName.getCode());
  }

}
