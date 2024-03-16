package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.service.EducationService;
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
 * Controller class for handling HTTP requests related to education entities.
 * Acts as an interface between HTTP requests and the EducationService.
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
   * Creates a new education entity.
   *
   * @param userId           The ID of the user to whom the education belongs.
   * @param educationDto The data representing the education entity to be created.
   * @return EducationDto representing the newly created education entity.
   */
  @PostMapping("/{userId}")
  public EducationDto create(@PathVariable int userId, @RequestBody EducationDto educationDto) {
    return educationService.create(userId, educationDto);
  }

  /**
   * Updates an existing education entity.
   *
   * @param id           The ID of the education entity to be updated.
   * @param educationDto The updated data for the education entity.
   * @return EducationDto representing the updated education entity.
   */
  @PutMapping("/{id}")
  public EducationDto update(@PathVariable int id, @RequestBody EducationDto educationDto) {
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
