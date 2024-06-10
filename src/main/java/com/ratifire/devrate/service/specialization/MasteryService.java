package com.ratifire.devrate.service.specialization;

import com.ratifire.devrate.dto.MasteryDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.MasteryRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s Mastery.
 */
@Service
@RequiredArgsConstructor
public class MasteryService {

  private final MasteryRepository masteryRepository;
  private final DataMapper<MasteryDto, Mastery> masteryMapper;
  private final DataMapper<SkillDto, Skill> skillMapper;

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

    return masteryMapper.toDto(mastery);
  }

  /**
   * Retrieves list of skills by MasteryID.
   *
   * @param id the ID of the Mastery
   * @return list of skills as dto.
   * @throws ResourceNotFoundException if Mastery is not found
   */
  public List<SkillDto> getSkillsByMasteryId(long id) {
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
    skill.setAverageMark(BigDecimal.ONE);
    mastery.getSkills().add(skill);
    masteryRepository.save(mastery);
    return skillMapper.toDto(skill);
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
}