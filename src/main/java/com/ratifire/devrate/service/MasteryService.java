package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.dto.SkillSetDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryRepository;
import com.ratifire.devrate.service.interview.InterviewMetricsService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

  private final InterviewMetricsService interviewMetricsService;
  private final SkillService skillService;
  private final MasteryRepository masteryRepository;
  private final DataMapper<MasteryDto, Mastery> masteryMapper;
  private final DataMapper<SkillDto, Skill> skillMapper;

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
   * Retrieves Mastery by ID.
   *
   * @param id the ID of the Mastery
   * @return the Mastery as a DTO
   * @throws ResourceNotFoundException if Mastery is not found
   */
  public MasteryDto findById(long id) {
    return masteryMapper.toDto(getMasteryById(id));
  }

  public List<Mastery> findByIds(List<Long> ids) {
    return masteryRepository.findByIds(ids);
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
   * Retrieves a SkillSetDto containing hard and soft skills associated with a given Mastery ID.
   *
   * @param id the ID of the Mastery
   * @return a SkillSetDto with lists of hard and soft skills
   */
  public SkillSetDto getAllSkillsByMasteryId(Long id) {
    Map<SkillType, List<SkillDto>> groupedSkills = getSkillsByMasteryId(id).stream()
        .collect(Collectors.groupingBy(SkillDto::getType));

    return new SkillSetDto(
        groupedSkills.getOrDefault(SkillType.HARD_SKILL, List.of()),
        groupedSkills.getOrDefault(SkillType.SOFT_SKILL, List.of())
    );
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
   * Updates existing Mastery.
   *
   * @param mastery the updated Mastery
   */
  public void update(Mastery mastery) {
    masteryRepository.save(mastery);
  }

  /**
   * Creates a skill and associates it with a mastery.
   *
   * @param skillDto  the skill information as a DTO
   * @param masteryId the ID of the mastery to associate with the skill
   * @return the created skill information as a DTO
   */
  public SkillDto create(SkillDto skillDto, long masteryId) {
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
    interviewMetricsService.updateMasteryAverageMark(mastery, skill.getType());
    update(mastery);
    return skillMapper.toDto(skill);
  }

  /**
   * Creates a skills and associates it with a mastery.
   *
   * @param skillDtos the list of skills information as a DTO
   * @param masteryId the ID of the mastery to associate with the skill
   * @return the created skill information as a DTO
   */
  public List<SkillDto> createBulk(List<SkillDto> skillDtos, long masteryId) {
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
    interviewMetricsService.updateMasteryAverageMark(mastery, skillDtos.getFirst().getType());
    update(mastery);
    return skillMapper.toDto(skills);
  }

  /**
   * Set skills for mastery.
   */
  public void setSkillsForMastery(Mastery mastery) {
    mastery.setSkills(skillService.loadSoftSkills());
    masteryRepository.save(mastery);
  }
}