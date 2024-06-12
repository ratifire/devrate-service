package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.service.specialization.SkillService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for CRUD operations with skill.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {

  private final SkillService skillService;

  /**
   * Retrieves Skill by ID.
   *
   * @return the skill as a DTO
   */
  @GetMapping("/{id}")
  public SkillDto findById(@PathVariable Long id) {
    return skillService.findById(id);
  }

  /**
   * Calculates averageMark and update skill information: averageMark, counter and grows.
   *
   * @param id   - the ID of the skill.
   * @param mark - the new mark to be included in the average calculation.
   * @return updated skill as a DTO
   */
  @PutMapping("/{id}/calculate-mark/{mark}")
  public SkillDto updateMark(@PathVariable long id, @PathVariable BigDecimal mark) {
    return skillService.updateMark(id, mark);
  }

  /**
   * Delete skill by ID.
   *
   * @param id the ID of skill
   */
  @DeleteMapping("/{id}")
  public void deleteById(@PathVariable long id) {
    skillService.delete(id);
  }
}