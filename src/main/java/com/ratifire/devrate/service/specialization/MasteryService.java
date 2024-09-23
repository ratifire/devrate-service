package com.ratifire.devrate.service.specialization;

import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.MasteryHistoryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.MasteryHistory;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryHistoryRepository;
import com.ratifire.devrate.repository.MasteryRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s Mastery.
 */
@Service
@RequiredArgsConstructor
@Named("MasteryService")
public class MasteryService {

  private final MasteryRepository masteryRepository;
  private final SkillService skillService;
  private final DataMapper<MasteryDto, Mastery> masteryMapper;
  private final DataMapper<SkillDto, Skill> skillMapper;
  private final MasteryHistoryRepository masteryHistoryRepository;
  private final DataMapper<MasteryHistoryDto, MasteryHistory> masteryHistoryMapper;

  /**
   * Retrieves Mastery by ID.
   *
   * @param id the ID of the Mastery
   * @return the Mastery as a DTO
   * @throws ResourceNotFoundException if Mastery is not found
   */
  public MasteryDto findById(long id) {
    return masteryMapper.toDto(getMasteryById(id));
  }

  /**
   * Retrieves Mastery by ID.
   *
   * @param id the ID of the Mastery
   * @return the Mastery as entity
   * @throws ResourceNotFoundException if Mastery is not found
   */
  @Named("getMasteryById")
  public Mastery getMasteryById(long id) {
    return masteryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Mastery not found with id: " + id));
  }

  /**
   * Retrieves Mastery by Skill ID.
   *
   * @param skillId the ID of the Skill
   * @return the Mastery as entity
   */
  public Mastery findMasteryBySkillId(long skillId) {
    return masteryRepository.findMasteryBySkillId(skillId);
  }

  /**
   * Updates existing Mastery.
   *
   * @param mastery the updated Mastery
   */
  public void updateMastery(Mastery mastery) {
    masteryRepository.save(mastery);
  }

  /**
   * Retrieves list of softSkills by MasteryID.
   *
   * @param id the ID of the Mastery
   * @return list of softSkills as dto.
   * @throws ResourceNotFoundException if Mastery is not found
   */
  public List<SkillDto> getSoftSkillsByMasteryId(Long id) {
    return getSkillsByMasteryId(id).stream()
        .filter(skillDto -> skillDto.getType() == SkillType.SOFT_SKILL)
        .toList();
  }

  /**
   * Retrieves list of hardSkills by MasteryID.
   *
   * @param id the ID of the Mastery
   * @return list of hardSkills as dto.
   * @throws ResourceNotFoundException if Mastery is not found
   */
  public List<SkillDto> getHardSkillsByMasteryId(Long id) {
    return getSkillsByMasteryId(id).stream()
        .filter(skillDto -> skillDto.getType() == SkillType.HARD_SKILL)
        .toList();
  }

  /**
   * Retrieves list of skills by MasteryID.
   *
   * @param id the ID of the Mastery
   * @return list of skills as dto.
   * @throws ResourceNotFoundException if Mastery is not found
   */
  private List<SkillDto> getSkillsByMasteryId(long id) {
    Mastery mastery = getMasteryById(id);
    return skillMapper.toDto(mastery.getSkills());
  }

  /**
   * Creates a skill and associates it with a mastery.
   *
   * @param skillDto  the skill information as a DTO
   * @param masteryId the ID of the mastery to associate with the skill
   * @return the created skill information as a DTO
   */
  public SkillDto createSkill(SkillDto skillDto, long masteryId) {
    if (skillDto == null) {
      throw new ResourceNotFoundException("The skill is a required param.");
    }

    if (masteryRepository.existsByIdAndSkills_Name(masteryId, skillDto.getName())) {
      throw new ResourceAlreadyExistException(
          "Skill with name " + skillDto.getName() + " already exists");
    }
    Mastery mastery = getMasteryById(masteryId);
    Skill skill = skillMapper.toEntity(skillDto);
    skill.setAverageMark(BigDecimal.ZERO);
    mastery.getSkills().add(skill);
    refreshMasteryAverageMark(mastery, skill.getType());
    updateMastery(mastery);
    return skillMapper.toDto(skill);
  }

  /**
   * Creates a skills and associates it with a mastery.
   *
   * @param skillDtos the list of skills information as a DTO
   * @param masteryId the ID of the mastery to associate with the skill
   * @return the created skill information as a DTO
   */
  public List<SkillDto> createSkills(List<SkillDto> skillDtos, long masteryId) {
    List<Skill> skills = skillDtos.stream()
        .filter(skill -> !masteryRepository.existsByIdAndSkills_Name(masteryId, skill.getName()))
        .map(skillMapper::toEntity)
        .peek(skill -> skill.setAverageMark(BigDecimal.ZERO))
        .toList();

    if (skills.isEmpty()) {
      return skillMapper.toDto(skills);
    }

    Mastery mastery = getMasteryById(masteryId);
    mastery.getSkills().addAll(skills);
    refreshMasteryAverageMark(mastery, skillDtos.getFirst().getType());
    updateMastery(mastery);
    return skillMapper.toDto(skills);
  }

  /**
   * Recalculates and updates the average mark for a mastery based on the specified type of skills.
   *
   * @param mastery   the mastery whose average mark should be refreshed
   * @param skillType the type of skills (soft or hard) to consider for the average mark
   */
  public void refreshMasteryAverageMark(Mastery mastery, SkillType skillType) {
    List<Skill> skills = mastery.getSkills().stream()
        .filter(s -> s.getType() == skillType)
        .toList();

    BigDecimal newAverageMark = calculateAverageMark(skills);

    if (skillType == SkillType.SOFT_SKILL) {
      mastery.setSoftSkillMark(newAverageMark);
    } else {
      mastery.setHardSkillMark(newAverageMark);
    }
  }

  /**
   * Set skills for mastery.
   */
  public void setSkillsForMastery(Mastery mastery) {
    mastery.setSkills(skillService.loadSoftSkills());
    masteryRepository.save(mastery);
  }

  /**
   * Saves the current state of the Mastery entity into the history.
   *
   * @param mastery the Mastery entity whose current state is to be saved in the history.
   */
  private void saveHistory(Mastery mastery) {
    MasteryHistory history = MasteryHistory.builder()
        .mastery(mastery)
        .date(LocalDate.now())
        .hardSkillMark(mastery.getHardSkillMark())
        .softSkillMark(mastery.getSoftSkillMark())
        .build();
    masteryHistoryRepository.save(history);
  }

  /**
   * Retrieves the history of a Mastery within a specified date range.
   *
   * @param masteryId the ID of the Mastery to retrieve history for
   * @param from the start date of the date range (inclusive)
   * @param to the end date of the date range (inclusive)
   * @return a list of MasteryHistoryDto containing the history entries for the specified
   *         Mastery ID within the given date range
   */
  public List<MasteryHistoryDto> getMasteryHistory(Long masteryId, LocalDate from, LocalDate to) {
    List<MasteryHistory> histories = masteryHistoryRepository
        .findByMasteryIdAndDateBetween(masteryId, from, to);
    return masteryHistoryMapper.toDto(histories);
  }

  /**
   * Updates the mastery marks after getting feedback for both soft and potentially hard skills
   * based on the role of the reviewer.
   *
   * @param masteryId    The ID of the Mastery entity to be updated.
   * @param reviewerRole The role of the reviewer, which determines whether hard skills should also
   *                     be updated.
   */
  public void updateMasteryAverageMarksAfterGettingFeedback(long masteryId,
      InterviewRequestRole reviewerRole) {
    Mastery mastery = getMasteryById(masteryId);

    BigDecimal updatedSoftSkillMark = calculateAverageMark(
        skillMapper.toEntity(getSoftSkillsByMasteryId(masteryId)));

    mastery.setSoftSkillMark(updatedSoftSkillMark);

    if (reviewerRole == INTERVIEWER) {
      BigDecimal updatedHardSkillMark = calculateAverageMark(
          skillMapper.toEntity(getHardSkillsByMasteryId(masteryId)));
      mastery.setHardSkillMark(updatedHardSkillMark);
    }

    updateMastery(mastery);
    saveHistory(mastery);
  }

  private BigDecimal calculateAverageMark(List<Skill> skills) {
    BigDecimal sumAverageSkillsMarks = skills.stream()
        .map(Skill::getAverageMark)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (skills.isEmpty()) {
      return BigDecimal.ZERO;
    }

    return sumAverageSkillsMarks.divide(BigDecimal.valueOf(skills.size()), 2,
        RoundingMode.HALF_UP);
  }
}