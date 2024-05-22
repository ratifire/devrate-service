package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.NicheLevelDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.service.NicheLevelService;
import com.ratifire.devrate.service.SkillService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for CRUD operations with niche level.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/niche-levels")
public class NicheLevelController {

  private final NicheLevelService nicheLevelService;
  private final SkillService skillService;

  /**
   * Retrieves niche level by ID.
   *
   * @return the niche level as a DTO
   */
  @GetMapping("/{id}")
  public NicheLevelDto findById(@PathVariable long id) {
    return nicheLevelService.findById(id);
  }

  /**
   * Retrieves list of skills by level ID.
   *
   * @return the niche as a DTO
   */
  @GetMapping("/{levelId}/skills")
  public List<SkillDto> getSkillsByNicheLevelId(@PathVariable long levelId) {
    return nicheLevelService.getSkillsByLevelId(levelId);
  }

  /**
   * Creates niche level`s skill by level ID.
   *
   * @param skillDto the skill information as a DTO
   * @return the created skill as a DTO
   */
  @PostMapping("/{levelId}/skills")
  public SkillDto createSkill(@Valid @RequestBody SkillDto skillDto,
      @PathVariable long levelId) {
    return nicheLevelService.createSkill(skillDto, levelId);
  }

  /**
   * Deleted skill by ID.
   */
  @GetMapping("/skill/{id}")
  public void deletedSkillById(@PathVariable long id) {
    skillService.deleteById(id);
  }

}
