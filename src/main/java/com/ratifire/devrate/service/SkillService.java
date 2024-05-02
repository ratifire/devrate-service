package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.exception.SkillNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.SkillRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s skills.
 */
@Service
@RequiredArgsConstructor
public class SkillService {

  private final SkillRepository skillRepository;
  private final DataMapper<SkillDto, Skill> skillDtoSkillDataMapper;

  /**
   * Retrieves skill by ID.
   *
   * @param id the ID of the skill
   * @return the user's skill as a DTO
   * @throws SkillNotFoundException if skill information is not found
   */
  public SkillDto findById(long id) {
    return skillRepository.findById(id).map(skillDtoSkillDataMapper::toDto)
        .orElseThrow(
            () -> new SkillNotFoundException("Skill not found with id: "
                + id));
  }

  /**
   * Updates user skill information.
   *
   * @param skillDto the updated user's skill information as a DTO
   * @return the updated user skill information as a DTO
   * @throws SkillNotFoundException if the user skill info does not exist by skill`s id
   */
  public SkillDto update(SkillDto skillDto) {
    long id = skillDto.getId();
    Optional<Skill> optionalSkill = skillRepository.findById(id);

    return optionalSkill.map(skill -> {
      skillDtoSkillDataMapper.updateEntity(skillDto, skill);
      Skill updatedSkill = skillRepository.save(skill);
      return skillDtoSkillDataMapper.toDto(updatedSkill);
    }).orElseThrow(() -> new SkillNotFoundException("Skill`s id: " + id));
  }

  /**
   * Deletes skill information by skill ID.
   *
   * @param id the ID of the skill whose skill information is to be deleted
   */
  public void deleteById(long id) {
    skillRepository.deleteById(id);
  }

}
