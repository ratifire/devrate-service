package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.dto.SkillFeedbackDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SkillRepository;
import com.ratifire.devrate.util.JsonConverter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
   * Finds and returns a list of skills by their IDs.
   *
   * @param ids the list of skill IDs to retrieve
   * @return a list of Skill entities corresponding to the given IDs
   * @throws ResourceNotFoundException if any of the skills with the given IDs are not found
   */
  public List<Skill> findAllById(List<Long> ids) {
    return repository.findAllById(ids);
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
   * Updates the marks after getting feedback for a list of skills based on the provided feedback.
   *
   * @param skillFeedbackDtos a list of SkillFeedbackDto objects containing the skill IDs and their
   *                          corresponding new marks provided as feedback
   */
  public void updateMarks(List<SkillFeedbackDto> skillFeedbackDtos) {
    List<Long> skillIds = skillFeedbackDtos.stream()
        .map(SkillFeedbackDto::getId)
        .toList();

    Map<Long, BigDecimal> skillIdToMarkMap = skillFeedbackDtos.stream()
        .collect(Collectors.toMap(SkillFeedbackDto::getId, SkillFeedbackDto::getMark));

    List<Skill> skills = repository.findAllById(skillIds);

    skills.forEach(skill -> {
      BigDecimal newMark = skillIdToMarkMap.get(skill.getId());
      BigDecimal oldAverageMark = skill.getAverageMark();
      long counter = skill.getCounter();

      BigDecimal newAverageMark = counter > 0
          ? calculateAverageMark(counter, oldAverageMark, newMark)
          : newMark;

      setMarkCounterGrowAndSave(skill, newAverageMark, counter + 1);
    });
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
          masteryService.refreshMasteryAverageMark(mastery, skill.getType());
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
        .collect(Collectors.toList());
  }

  /**
   * Calculates the new average mark for the skill.
   *
   * @param counter the current counter value for the skill
   * @param oldMark the current average mark for the skill
   * @param newMark the new mark to be added to the average
   * @return the new calculated average mark
   */
  private BigDecimal calculateAverageMark(long counter, BigDecimal oldMark, BigDecimal newMark) {
    BigDecimal oldNumberOfMarks = oldMark.multiply(BigDecimal.valueOf(counter));
    BigDecimal newNumberOfMarks = oldNumberOfMarks.add(newMark);
    return newNumberOfMarks.divide(BigDecimal.valueOf(counter + 1), 2,
        RoundingMode.HALF_UP);
  }

  /**
   * Sets the mark, counter, and grows status for the skill and saves it.
   *
   * @param skill   the skill to be updated and saved
   * @param mark    the new mark to set for the skill
   * @param counter the new counter value to set for the skill
   */
  private void setMarkCounterGrowAndSave(Skill skill, BigDecimal mark, long counter) {
    int comparisonResult = skill.getAverageMark().compareTo(mark);
    skill.setGrows(comparisonResult <= 0);
    skill.setAverageMark(mark);
    skill.setCounter(counter);
    repository.save(skill);
  }
}