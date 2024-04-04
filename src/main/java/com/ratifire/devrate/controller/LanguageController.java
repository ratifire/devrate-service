package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.LanguageDto;
import com.ratifire.devrate.service.LanguageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling language-related endpoints. Provides CRUD operations for managing
 * languages associated with users.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/languages")
public class LanguageController {

  private final LanguageService languageService;

  /**
   * Retrieves a language by its ID.
   *
   * @param id the ID of the language to retrieve
   * @return LanguageDto representing the language entity.
   */
  @GetMapping("/{id}")
  public LanguageDto findById(@PathVariable long id) {
    return languageService.findById(id);
  }

  /**
   * Creates a new language for a user.
   *
   * @param userId      the ID of the user to associate the new language with
   * @param languageDto the language data to create
   * @return LanguageDto the created language data
   */
  @PostMapping("/{userId}")
  public LanguageDto create(@PathVariable long userId,
      @Valid @RequestBody LanguageDto languageDto) {
    return languageService.create(userId, languageDto);
  }

  /**
   * Updates an existing language.
   *
   * @param id          the ID of the language to update
   * @param languageDto the updated language data
   * @return LanguageDto the updated language data
   */
  @PutMapping("/{id}")
  public LanguageDto update(@PathVariable long id, @Valid @RequestBody LanguageDto languageDto) {
    return languageService.update(id, languageDto);
  }

  /**
   * Deletes a language by its ID.
   *
   * @param id the ID of the language to delete
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    languageService.delete(id);
  }

}