package com.ratifire.devrate.service.specialization;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.MasteryHistoryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.MasteryHistory;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryHistoryRepository;
import com.ratifire.devrate.repository.MasteryRepository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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

  private final MasteryRepository masteryRepository;
  private final SkillService skillService;
  private final DataMapper<MasteryDto, Mastery> masteryMapper;
  private final DataMapper<SkillDto, Skill> skillMapper;
  private final MasteryHistoryRepository masteryHistoryRepository;

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
   * Updates Mastery information: HardSkillMark and SoftSkillMark.
   *
   * @param masteryDto the updated Mastery as a DTO
   * @return the updated Mastery as a DTO
   */
  public MasteryDto update(MasteryDto masteryDto) {
    long id = masteryDto.getId();
    Mastery mastery = getMasteryById(id);

    masteryMapper.updateEntity(masteryDto, mastery);
    masteryRepository.save(mastery);
    saveHistory(mastery);

    return masteryMapper.toDto(mastery);
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
        .collect(Collectors.toList());
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
        .collect(Collectors.toList());
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
  public SkillDto createSkill(SkillDto skillDto,
      long masteryId) {
    Mastery mastery = getMasteryById(masteryId);
    existSkillByName(masteryId, skillDto.getName());
    Skill skill = skillMapper.toEntity(skillDto);
    skill.setAverageMark(BigDecimal.ZERO);
    mastery.getSkills().add(skill);
    masteryRepository.save(mastery);
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
    Mastery mastery = getMasteryById(masteryId);
    skillDtos.forEach(dto -> existSkillByName(masteryId, dto.getName()));
    List<Skill> skills = skillMapper.toEntity(skillDtos);
    skills.forEach(skill -> skill.setAverageMark(BigDecimal.ZERO));
    mastery.getSkills().addAll(skills);
    masteryRepository.save(mastery);
    return skillMapper.toDto(skills);
  }

  /**
   * Checks if a skill with the given name already exists in the specified mastery.
   *
   * @param id   the ID of the mastery to check
   * @param name the name of the skill to check for uniqueness
   * @throws ResourceAlreadyExistException if a skill with the given name already exists
   */
  private void existSkillByName(long id, String name) {
    if (masteryRepository.existsByIdAndSkills_Name(id, name)) {
      throw new ResourceAlreadyExistException("Skill with name " + name + " already exists");
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
        .timestamp(new Date())
        .hardSkillMark(mastery.getHardSkillMark())
        .softSkillMark(mastery.getSoftSkillMark())
        .build();
    masteryHistoryRepository.save(history);
  }

  /**
   * Retrieves a list of MasteryHistoryDto entries for a given Mastery ID,
   * sorted by timestamp in descending order.
   *
   * @param masteryId the ID of the Mastery for which history is requested
   * @return a list of MasteryHistoryDto, can be empty if no history found
   */
  public List<MasteryHistoryDto> getMasteryHistory(Long masteryId) {
    return masteryHistoryRepository.findHistoriesByMasteryId(masteryId);
  }
}