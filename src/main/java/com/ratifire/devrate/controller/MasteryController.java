package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.MasteryHistoryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.MasteryHistory;
import com.ratifire.devrate.service.specialization.MasteryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for CRUD operations with Mastery.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/masteries")
public class MasteryController {

  private final MasteryService masteryService;

  /**
   * Retrieves Mastery by ID.
   *
   * @return the Mastery as a DTO
   */
  @GetMapping("/{id}")
  public MasteryDto findById(@PathVariable Long id) {
    return masteryService.findById(id);
  }

  /**
   * Retrieves list of hard skills by MasteryID.
   *
   * @return the list of skills as a DTO
   */
  @GetMapping("/{id}/hard-skills")
  public List<SkillDto> getHardSkillsByMasteryId(@PathVariable Long id) {
    return masteryService.getHardSkillsByMasteryId(id);
  }

  /**
   * Retrieves list of soft skills by MasteryID.
   *
   * @return the list of skills as a DTO
   */
  @GetMapping("/{id}/soft-skills")
  public List<SkillDto> getSoftSkillsByMasteryId(@PathVariable Long id) {
    return masteryService.getSoftSkillsByMasteryId(id);
  }

  /**
   * Creates a skill and associates it with the specified Mastery by its ID.
   *
   * @param skillDto the skill information as a DTO.
   * @param id       the ID of the Mastery to which the skill will be associated.
   * @return the created skill information as a DTO.
   */
  @PostMapping("/{id}/skills")
  public SkillDto createSkill(
      @Valid @RequestBody SkillDto skillDto, @PathVariable long id) {
    return masteryService.createSkill(skillDto, id);
  }

  /**
   * Creates a skills and associates it with the specified Mastery by its ID.
   *
   * @param skillDtos the list of skills information as a DTO.
   * @param id        the ID of the Mastery to which the skill will be associated.
   * @return the created skills information as a DTO.
   */
  @PostMapping("/{id}/skills/bulk")
  public List<SkillDto> createSkills(
      @Valid @RequestBody List<SkillDto> skillDtos, @PathVariable long id) {
    return masteryService.createSkills(skillDtos, id);
  }

  /**
   * Updates Mastery by ID.
   *
   * @param masteryDto the updated Mastery information as a DTO
   * @return the updated Mastery information as a DTO
   */
  @PutMapping()
  public MasteryDto update(@RequestBody MasteryDto masteryDto) {
    return masteryService.update(masteryDto);
  }

  /**
   * Endpoint to retrieve the history of a Mastery by its ID.
   *
   * @param masteryId the ID of the Mastery to retrieve history for
   * @return List of MasteryHistory
   */
  @GetMapping("/{masteryId}/history")
  public List<MasteryHistory> getMasteryHistory(@PathVariable Long masteryId) {
    return masteryService.getMasteryHistory(masteryId);
  }
}