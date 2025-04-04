package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    return skillService.getSkillDtoById(id);
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

  /**
   * Hides or unhides a skill based on the provided ID and hide status.
   *
   * @param id   the ID of the skill to be hidden or unhidden
   * @param hide the flag indicating whether to hide (true) or unhide (false) the skill
   * @return a {@link SkillDto} object representing the updated skill
   */
  @PatchMapping("/{id}/hide/{hide}")
  public SkillDto hideSkill(@PathVariable long id, @PathVariable boolean hide) {
    return skillService.updateHiddenStatus(id, hide);
  }
}