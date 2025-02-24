package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SkillRepository;
import com.ratifire.devrate.service.interview.InterviewMetricsService;
import com.ratifire.devrate.util.JsonConverter;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The service responsible for managing user`s Skill.
 */
@Service
@RequiredArgsConstructor
public class SkillService {

  @Value("${skill.defaultSoftSkillsPath}")
  private String defaultSoftSkillsPath;

  private MasteryService masteryService;
  private final InterviewMetricsService interviewMetricsService;
  private final SkillRepository repository;
  private final DataMapper<SkillDto, Skill> mapper;

  @Autowired
  public void setMasteryService(@Lazy MasteryService masteryService) {
    this.masteryService = masteryService;
  }

  /**
   * Retrieves Skill by ID.
   *
   * @param id the ID of the Skill
   * @return the Skill as entity
   * @throws ResourceNotFoundException if Skill is not found
   */
  private Skill findById(long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
  }

  /**
   * Retrieves SkillDto by ID.
   *
   * @param id the ID of the Skill
   * @return the Skill as a DTO
   * @throws ResourceNotFoundException if Skill is not found
   */
  public SkillDto getSkillDtoById(long id) {
    return mapper.toDto(findById(id));
  }

  /**
   * Updates the hide status of a skill identified by its ID.
   *
   * @param id   the ID of the skill to be hidden or unhidden
   * @param hide the flag indicating whether to hide (true) or unhide (false) the skill
   * @return a {@link SkillDto} object representing the updated skill
   */
  public SkillDto updateHiddenStatus(long id, boolean hide) {
    Skill skill = findById(id);
    skill.setHidden(hide);
    return mapper.toDto(repository.save(skill));
  }

  /**
   * Deletes a skill by its ID and updates the mastery average marks.
   *
   * @param id the ID of the skill to be deleted
   */
  @Transactional
  public void delete(long id) {
    Mastery mastery = masteryService.findMasteryBySkillId(id);

    mastery.getSkills().stream()
        .filter(s -> s.getId() == id)
        .findFirst()
        .ifPresent(skill -> {
          mastery.getSkills().remove(skill);
          interviewMetricsService.updateMasteryAverageMark(mastery, skill.getType());
          masteryService.update(mastery);
        });
  }

  /**
   * Generates a list of softSkills entities with default values of name and other using bean.
   */
  public List<Skill> loadSoftSkills() {
    return JsonConverter.loadStringFromJson(defaultSoftSkillsPath).stream()
        .map(skillName -> Skill.builder()
            .name(skillName)
            .counter(0)
            .averageMark(BigDecimal.ZERO)
            .grows(false)
            .type(SkillType.SOFT_SKILL)
            .build())
        .toList();
  }
}