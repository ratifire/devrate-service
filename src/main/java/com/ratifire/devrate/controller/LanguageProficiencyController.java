package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.service.LanguageProficiencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling language proficiency endpoints. Provides CRUD operations for
 * managing languages proficiencies associated with users.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/language-proficiencies")
public class LanguageProficiencyController {

  private final LanguageProficiencyService languageProficiencyService;

  /**
   * Retrieves a language proficiency by its ID.
   *
   * @param id the ID of the language proficiency to retrieve
   * @return LanguageProficiencyDto representing the language proficiency entity.
   */
  @GetMapping("/{id}")
  public LanguageProficiencyDto findById(@PathVariable long id) {
    return languageProficiencyService.findById(id);
  }

  /**
   * Updates an existing language proficiency with new data.
   *
   * @param id          the ID of the language proficiency to update
   * @param languageProficiencyDto the updated language proficiency data
   * @return LanguageProficiencyDto the updated language proficiency data
   */
  @PutMapping("/{id}")
  public LanguageProficiencyDto update(@PathVariable long id,
      @Valid @RequestBody LanguageProficiencyDto languageProficiencyDto) {
    return languageProficiencyService.update(id, languageProficiencyDto);
  }

  /**
   * Deletes a language proficiency by its ID.
   *
   * @param id the ID of the language proficiency to delete
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    languageProficiencyService.delete(id);
  }

}