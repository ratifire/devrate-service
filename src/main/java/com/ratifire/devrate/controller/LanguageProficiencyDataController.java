package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.LanguageProficiencyDataDto;
import com.ratifire.devrate.service.LanguageProficiencyDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to language proficiency information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/language-proficiencies-data")
public class LanguageProficiencyDataController {

  private final LanguageProficiencyDataService languageProficiencyDataService;

  /**
   * Retrieves a list of language proficiency data for dropdowns.
   *
   * @return a list of LanguageProficiencyDataDto containing language proficiency data.
   */
  @GetMapping
  public List<LanguageProficiencyDataDto> getAllLanguageProficiencyData() {
    return languageProficiencyDataService.getAllLanguageProficiencyData();
  }

}
