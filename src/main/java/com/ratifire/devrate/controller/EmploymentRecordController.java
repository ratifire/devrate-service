package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.service.employmentrecord.EmploymentRecordService;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping("user/{userId}/employment-record")
public class EmploymentRecordController {

  private final EmploymentRecordService employmentRecordService;

  /**
   * Retrieves user work experience information by user ID.
   *
   * @param userId the ID of the user
   * @return the user's work experience information as a DTO
   */
  @GetMapping
  public List<EmploymentRecordDto> findByUserId(@PathVariable long userId) {
    return employmentRecordService.findByUserId(userId);
  }

  /**
   * Creates user work experience information by user ID.
   *
   * @param employmentRecordDto the user's work experience information as a DTO
   * @return the created user work experience information as a DTO
   */
  @PostMapping
  public EmploymentRecordDto create(@Valid @RequestBody EmploymentRecordDto employmentRecordDto,
      @PathVariable long userId) {
    return employmentRecordService.create(employmentRecordDto, userId);
  }

  /**
   * Updates user work experience information by user ID.
   *
   * @param employmentRecordDto the updated user's work experience information as a DTO
   * @return the updated user work experience information as a DTO
   */
  @PutMapping
  public EmploymentRecordDto update(@RequestBody EmploymentRecordDto employmentRecordDto) {
    return employmentRecordService.update(employmentRecordDto);
  }

  /**
   * Deletes user work experience information by user ID.
   *
   * @param id the ID of the user
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    employmentRecordService.delete(id);
  }

}
