package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.service.specialization.SpecializationService;
import java.util.List;
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
   * Retrieves specialization by ID.
   *
   * @return the specialization as a DTO
   */
  @GetMapping("/{id}")
  public SpecializationDto findById(@PathVariable Long id) {
    return specializationService.findById(id);
  }

  /**
   * Retrieves main mastery by SpecializationID.
   *
   * @return the main mastery as a DTO
   */
  @GetMapping("/{id}/main-mastery")
  public MasteryDto getMainMasteryById(@PathVariable Long id) {
    return specializationService.getMainMasteryById(id);
  }

  /**
   * Retrieves list of masteries by SpecializationID.
   *
   * @return the list of masteries as a DTO
   */
  @GetMapping("/{id}/masteries")
  public List<MasteryDto> getMasteriesBySpecializationId(@PathVariable Long id) {
    return specializationService.getMasteriesBySpecializationId(id);
  }

  /**
   * Updates specialization name by specialization`s ID.
   *
   * @param specializationDto the updated specialization information as a DTO
   * @return the updated specialization information as a DTO
   */
  @PutMapping()
  public SpecializationDto update(@RequestBody SpecializationDto specializationDto) {
    return specializationService.update(specializationDto);
  }

  /**
   * Sets the main specialization status for the given specialization ID.
   *
   * @param id the ID of the specialization that will become the new main specialization
   * @return the updated new main specialization as a DTO
   */
  @PutMapping("/{id}/set-main")
  public SpecializationDto setAsMainById(@PathVariable long id) {
    return specializationService.setAsMainById(id);
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

  /**
   * Sets new main mastery.
   *
   * @return new main mastery as a dto.
   */
  @PutMapping("/{specId}/set-main-mastery/{masteryId}")
  public MasteryDto setMainMasteryById(@PathVariable long specId,
      @PathVariable long masteryId) {
    return specializationService.setMainMasteryById(specId, masteryId);
  }
}