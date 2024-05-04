package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SpecialisationDto;
import com.ratifire.devrate.service.SpecialisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for CRUD operations with Specialisation.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/specialisations")
public class SpecialisationController {

  private final SpecialisationService specialisationService;

  /**
   * Retrieves Specialisation by ID.
   *
   * @return the specialisation as a DTO
   */
  @GetMapping("/{id}")
  public SpecialisationDto findById(@PathVariable long id) {
    return specialisationService.findById(id);
  }

  /**
   * Updates user Specialisation information by Specialisation`s ID.
   *
   * @param specialisationDto the updated user's Specialisation information as a DTO
   * @return the updated user Specialisation information as a DTO
   */
  @PutMapping
  public SpecialisationDto update(@RequestBody SpecialisationDto specialisationDto) {
    return specialisationService.update(specialisationDto);
  }

  /**
   * Delete Specialisation by ID.
   *
   * @param id the ID of Specialisation
   */
  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable long id) {
    specialisationService.deleteById(id);
  }
}
