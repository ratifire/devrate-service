package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.ParticipantRequestDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.mapper.DataMapper;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for converting between ParticipantRequestDto and InterviewRequest entities.
 */
@Mapper(componentModel = "spring")
public abstract class ParticipantRequestMapper implements
    DataMapper<ParticipantRequestDto, InterviewRequest> {

  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "participantId", source = "entity.user.id")
  @Mapping(target = "specialization", source = "entity.mastery.specialization.name")
  @Mapping(target = "masteryLevel", source = "entity.mastery.level")
  @Mapping(target = "hardSkills", source = "entity.mastery.skills",
      qualifiedByName = "extractHardSkills")
  @Mapping(target = "softSkills", source = "entity.mastery.skills",
      qualifiedByName = "extractSoftSkills")
  @Mapping(target = "dates", source = "entity.availableDates", qualifiedByName = "convertToDates")
  public abstract ParticipantRequestDto toDto(InterviewRequest entity);

  /**
   * Extracts hard skill names from a list of skills.
   *
   * @param skills the list of hard skills to filter and extract names from.
   * @return a set of hard skill names.
   */
  @Named("extractHardSkills")
  protected Set<String> extractHardSkills(List<Skill> skills) {
    return extractSkills(skills, SkillType.HARD_SKILL);
  }

  /**
   * Extracts soft skill names from a list of skills.
   *
   * @param skills the list of soft skills to filter and extract names from.
   * @return a set of soft skill names.
   */
  @Named("extractSoftSkills")
  protected Set<String> extractSoftSkills(List<Skill> skills) {
    return extractSkills(skills, SkillType.SOFT_SKILL);
  }

  private Set<String> extractSkills(List<Skill> skills, SkillType type) {
    return skills.stream()
        .filter(skill -> skill.getType() == type)
        .map(Skill::getName)
        .collect(Collectors.toSet());
  }

  /**
   * Converts a list of {@link ZonedDateTime} objects to a set of {@link Date} objects.
   *
   * @param availableDates the list of {@link ZonedDateTime} objects to convert.
   * @return a {@link Set} of {@link Date} converted from a {@link List} of {@link ZonedDateTime}
   */
  @Named("convertToDates")
  protected Set<Date> convertToDates(List<ZonedDateTime> availableDates) {
    return availableDates.stream()
        .map(zdt -> Date.from(zdt.toInstant()))
        .collect(Collectors.toSet());
  }
}