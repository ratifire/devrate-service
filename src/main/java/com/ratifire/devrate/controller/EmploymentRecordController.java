package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.service.employmentrecord.EmploymentRecordService;
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
 * Controller class responsible for handling requests related to user EmploymentRecord information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/employment-records")
public class EmploymentRecordController {

  private final EmploymentRecordService employmentRecordService;

  /**
   * Retrieves user EmploymentRecord information by ID.
   *
   * @param id the ID of the EmploymentRecord
   * @return the user's EmploymentRecord information as a DTO
   */
  @GetMapping("/{id}")
  public EmploymentRecordDto findById(@PathVariable long id) {
    return employmentRecordService.findById(id);
  }

  /**
   * Updates user EmploymentRecord information by EmploymentRecord`s ID.
   *
   * @param id                  The ID of the employment record entity to be updated.
   * @param employmentRecordDto the updated user's EmploymentRecord information as a DTO
   * @return the updated user EmploymentRecord information as a DTO
   */
  @PutMapping("/{id}")
  public EmploymentRecordDto update(@PathVariable long id,
      @RequestBody @Valid EmploymentRecordDto employmentRecordDto) {
    return employmentRecordService.update(id, employmentRecordDto);
  }

  /**
   * Deletes user EmploymentRecord information by EmploymentRecord`s ID.
   *
   * @param id the ID of EmploymentRecord
   */
  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable long id) {
    employmentRecordService.deleteById(id);
  }
}
