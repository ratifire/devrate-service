package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.LanguageProficiencyDataDto;
import com.ratifire.devrate.enums.LanguageProficiencyName;
import com.ratifire.devrate.mapper.LanguageProficiencyDataMapper;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for retrieving language proficiency data.
 */
@Service
@RequiredArgsConstructor
public class LanguageProficiencyDataService {

  private final LanguageProficiencyDataMapper mapper;

  /**
   * Retrieves language proficiency data for all languages.
   *
   * @return a list of LanguageProficiencyDataDto containing the name, code, and levels for each
   *     language
   */
  public List<LanguageProficiencyDataDto> getAllLanguageProficiencyData() {
    return mapper.toDto(Arrays.asList(LanguageProficiencyName.values()));
  }

}
