package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.exception.ResourceNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SkillRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing skill.
 */
@Service
@RequiredArgsConstructor
public class SkillService {

  private final SkillRepository skillRepository;
  private final DataMapper<SkillDto, Skill> skillDataMapper;

  /**
   * Updates skill information.
   *
   * @param skillDto the updated skill as a DTO
   * @return the updated skill as a DTO
   * @throws ResourceNotFoundException if the skill does not exist by id
   */
  public SkillDto update(SkillDto skillDto, long id) {
    Optional<Skill> oldSkill = skillRepository.findById(id);

    return oldSkill.map(skill -> {
      skillDataMapper.updateEntity(skillDto, skill);
      Skill updatedSkill = skillRepository.save(skill);
      return skillDataMapper.toDto(updatedSkill);
    }).orElseThrow(() -> new ResourceNotFoundException("Skill`s id: " + id));
  }

  /**
   * Deleted skill by ID.
   *
   * @param id the ID of the skill whose is to be deleted
   */
  public void deleteById(long id) {
    skillRepository.deleteById(id);
  }

}
