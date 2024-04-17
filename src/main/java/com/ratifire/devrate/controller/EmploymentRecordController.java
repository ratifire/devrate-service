package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.service.employmentrecord.EmploymentRecordService;
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
   * Retrieves user EmploymentRecord information by user ID.
   *
   * @param id the ID of the user
   * @return the user's EmploymentRecord information as a DTO
   */
  @GetMapping("/{id}")
  public EmploymentRecordDto findById(@PathVariable long id) {
    return employmentRecordService.findById(id);
  }


  /**
   * Updates user work experience information by user ID.
   *
   * @param employmentRecordDto the updated user's EmploymentRecord information as a DTO
   * @return the updated user EmploymentRecord information as a DTO
   */
  @PutMapping
  public EmploymentRecordDto update(@RequestBody EmploymentRecordDto employmentRecordDto) {
    return employmentRecordService.update(employmentRecordDto);
  }

  /**
   * Deletes user EmploymentRecord information by user ID.
   *
   * @param id the ID of the user
   */
  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable long id) {
    employmentRecordService.deleteById(id);
  }

}
