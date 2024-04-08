package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.WorkExperienceDto;
import com.ratifire.devrate.service.workexperience.WorkExperienceService;
import java.util.List;
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
 * Controller class responsible for handling requests related to user work experience information.
 */
@RestController
@RequestMapping("/work-experience")
@RequiredArgsConstructor
public class WorkExperienceController {

  private final WorkExperienceService workExperienceService;

  /**
   * Retrieves user work experience information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's work experience information as a DTO
   */
  @GetMapping("/{userId}")
  public List<WorkExperienceDto> findByUserId(@PathVariable long userId) {
    return workExperienceService.findByUserId(userId);
  }

  /**
   * Creates user work experience information by user ID.
   *
   * @param workExperienceDto the user's work experience information as a DTO
   * @return the created user work experience information as a DTO
   */
  @PostMapping
  public WorkExperienceDto create(@RequestBody WorkExperienceDto workExperienceDto) {
    return workExperienceService.create(workExperienceDto);
  }

  /**
   * Updates user work experience information by user ID.
   *
   * @param workExperienceDto the updated user's work experience information as a DTO
   * @return the updated user work experience information as a DTO
   */
  @PutMapping
  public WorkExperienceDto update(@RequestBody WorkExperienceDto workExperienceDto) {
    return workExperienceService.update(workExperienceDto);
  }

  /**
   * Deletes user work experience information by user ID.
   *
   * @param id the ID of the user
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    workExperienceService.delete(id);
  }

}
