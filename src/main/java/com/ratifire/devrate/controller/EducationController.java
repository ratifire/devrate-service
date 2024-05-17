package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.service.EducationService;
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
 * Controller class for handling HTTP requests related to education entities. Acts as an interface
 * between HTTP requests and the EducationService.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/educations")
public class EducationController {

  private final EducationService educationService;

  /**
   * Retrieves an education entity by its ID.
   *
   * @param id The ID of the education entity to retrieve.
   * @return EducationDto representing the education entity with the specified ID.
   */
  @GetMapping("/{id}")
  public EducationDto getById(@PathVariable long id) {
    return educationService.getById(id);
  }

  /**
   * Updates an existing education entity.
   *
   * @param id           The ID of the education entity to be updated.
   * @param educationDto The updated data for the education entity.
   * @return EducationDto representing the updated education entity.
   */
  @PutMapping("/{id}")
  public EducationDto update(@PathVariable long id, @RequestBody @Valid EducationDto educationDto) {
    return educationService.update(id, educationDto);
  }

  /**
   * Deletes an education entity by its ID.
   *
   * @param id The ID of the education entity to be deleted.
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    educationService.delete(id);
  }
}
