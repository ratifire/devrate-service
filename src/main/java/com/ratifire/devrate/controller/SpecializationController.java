package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.service.specialization.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for CRUD operations with Specialization.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/specializations")
public class SpecializationController {

  private final SpecializationService specializationService;

  /**
   * Retrieves specialization by USER_ID and specialization ID.
   *
   * @return the specialization as a DTO
   */
  @GetMapping("/{userId}/{id}")
  public SpecializationDto findById(@PathVariable Long userId, @PathVariable Long id) {
    return specializationService.findByUserIdAndId(userId, id);
  }

  /**
   * Updates specialization by specialization`s ID.
   *
   * @param specializationDto the updated specialization information as a DTO
   * @return the updated specialization information as a DTO
   */
  @PutMapping("/{id}")
  public SpecializationDto update(@RequestBody SpecializationDto specializationDto,
      @PathVariable long id) {
    return specializationService.update(specializationDto, id);
  }

  /**
   * Sets the main specialization status for the given specialization ID.
   *
   * @param specializationId the ID of the specialization that will become the new main
   *                                specialization
   * @return the updated new main specialization as a DTO
   */
  @PutMapping("/{specializationId}/set-main")
  public SpecializationDto setAsMainById(@PathVariable long specializationId) {
    return specializationService.setAsMainById(specializationId);
  }

  /**
   * Delete specialization by ID.
   *
   * @param id the ID of specialization
   */
  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable long id) {
    specializationService.deleteById(id);
  }

}
