package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NicheLevelDto;
import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.entity.NicheLevel;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.exception.NicheNotFoundException;
import com.ratifire.devrate.exception.ResourceAlreadyExistException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.NicheLevelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service responsible for managing user`s Niche.
 */
@Service
@RequiredArgsConstructor
public class NicheLevelService {

  private final NicheLevelRepository nicheLevelRepository;
  private final DataMapper<NicheLevelDto, NicheLevel> nicheLevelDataMapper;
  private final DataMapper<SkillDto, Skill> skillDataMapper;

  /**
   * Retrieves niche level by ID.
   *
   * @param id the ID of the Niche
   * @return the user's Niche as a DTO
   * @throws NicheNotFoundException if Niche is not found
   */
  public NicheLevelDto findById(long id) {
    return nicheLevelRepository.findById(id).map(nicheLevelDataMapper::toDto)
        .orElseThrow(
            () -> new NicheNotFoundException("Niche not found with id: "
                + id));
  }

  public List<SkillDto> getSkillsByLevelId(long id) {
    NicheLevel nicheLevel = findNicheLevelById(id);
    return skillDataMapper.toDto(nicheLevel.getSkills());
  }

  /**
   * Updates niche level.
   *
   * @param nicheLevelDto the updated niche level as a DTO
   * @return the updated niche level as a DTO
   */
  public NicheLevelDto update(NicheLevelDto nicheLevelDto) {
    NicheLevel nicheLevel = findNicheLevelById(nicheLevelDto.getId());
    nicheLevelDataMapper.updateEntity(nicheLevelDto, nicheLevel);
    return nicheLevelDataMapper.toDto(nicheLevelRepository.save(nicheLevel));
  }

  /**
   * Updates niche level.
   *
   * @param nicheLevel the updated niche level as a DTO
   * @return the updated niche level as a DTO
   */
  public NicheLevel updateNicheLevel(NicheLevel nicheLevel) {
    return nicheLevelRepository.save(nicheLevel);
  }

  /**
   * Retrieves a niche level entity by ID.
   *
   * @param id the ID of the niche level to retrieve
   * @return the user entity
   * @throws UserNotFoundException if the niche level with the specified ID is not found
   */
  private NicheLevel findNicheLevelById(long id) {
    return nicheLevelRepository.findById(id)
        .orElseThrow(() -> new NicheNotFoundException("The Niche not found with id " + id));
  }

  /**
   * Create skill by niche level ID.
   *
   * @param levelId the ID of the niche identifier for associate the skill with the niche
   */
  public SkillDto createSkill(SkillDto skillDto, long levelId) {
    checkSkillNameExistsForLevel(skillDto, levelId);
    NicheLevel nicheLevel = findNicheLevelById(levelId);
    Skill skill = skillDataMapper.toEntity(skillDto);
    nicheLevel.getSkills().add(skill);
    updateNicheLevel(nicheLevel);
    return skillDataMapper.toDto(skill);
  }

  /**
   * Check if the skill name exists at the niche level.
   *
   * @param levelId the ID of the niche identifier for associate the skill with the niche
   */
  private void checkSkillNameExistsForLevel(SkillDto skillDto, long levelId) {
    List<SkillDto> skills = getSkillsByLevelId(levelId);
    boolean skillExists = skills.stream().anyMatch(s -> s.getSkill().equals(skillDto.getSkill()));
    if (skillExists) {
      throw new ResourceAlreadyExistException("Skill with the same name "
          + "already exists in this niche level");
    }
  }

}
