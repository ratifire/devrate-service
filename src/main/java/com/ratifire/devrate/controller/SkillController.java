package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for CRUD operations with skills.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {

  private final SkillService skillService;

  /**
   * Retrieves skill by ID.
   *
   * @return the specialisation as a DTO
   */
  @GetMapping("/{id}")
  public SkillDto findById(@PathVariable long id) {
    return skillService.findById(id);
  }

  /**
   * Updates user skill information by skill`s ID.
   *
   * @param skillDto the updated user's skill information as a DTO
   * @return the updated user skill information as a DTO
   */
  @PutMapping
  public SkillDto update(@RequestBody SkillDto skillDto) {
    return skillService.update(skillDto);
  }

  /**
   * Delete skill by ID.
   *
   * @param id the ID of skill
   */
  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable long id) {
    skillService.deleteById(id);
  }
}
