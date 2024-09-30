package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.SkillDto;
import com.ratifire.devrate.dto.UserMainHardSkillsDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper interface for mapping between Specialization and UserMainHardSkillDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class UserMainHardSkillMapper implements
    DataMapper<UserMainHardSkillsDto, Specialization> {

  protected Map<Integer, String> defaultMasteryLevels;
  protected SkillMapper skillMapper;

  @Autowired
  protected void setDefaultMasteryLevels(Map<Integer, String> defaultMasteryLevels) {
    this.defaultMasteryLevels = defaultMasteryLevels;
  }

  @Autowired
  protected void setSkillMapper(SkillMapper skillMapper) {
    this.skillMapper = skillMapper;
  }

  @Mapping(target = "specializationName", source = "specialization.name")
  @Mapping(target = "isMainSpecialization", source = "specialization.main")
  @Mapping(target = "masteryName", source = "specialization.mainMastery.level",
      qualifiedByName = "getMainMasteryName")
  @Mapping(target = "hardSkills", source = "specialization.mainMastery.skills",
      qualifiedByName = "filterHardSkills")
  public abstract UserMainHardSkillsDto toDto(Specialization specialization);

  /**
   * Retrieves the name of the main mastery level based on the given level.
   *
   * @param level the mastery level to look up
   * @return the name of the corresponding mastery level, or "Unknown" if the level is not found
   */
  @Named("getMainMasteryName")
  public String getMainMasteryName(int level) {
    return defaultMasteryLevels.getOrDefault(level, "Unknown");
  }

  /**
   * Filters a list of skills to include only hard skills.
   *
   * @param skills the list of {@link Skill} objects to be filtered
   * @return a list of {@link SkillDto} objects that are classified as hard skills
   */
  @Named("filterHardSkills")
  public List<SkillDto> filterHardSkills(List<Skill> skills) {
    return skillMapper.toDto(skills.stream()
        .filter(skill -> skill.getType() == SkillType.HARD_SKILL)
        .toList());
  }
}
