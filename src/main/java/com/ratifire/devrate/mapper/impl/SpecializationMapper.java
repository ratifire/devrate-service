package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.entity.Specialization;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.util.converter.JsonUtil;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper interface for mapping between Specialization and SpecializationDto objects.
 */
@Mapper(componentModel = "spring")
public abstract class SpecializationMapper implements
    DataMapper<SpecializationDto, Specialization> {

  private static final String DEFAULT_MASTERY_LEVELS_PATH =
      "/static/data/specialization/mastery-levels.json";

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mainMastery", ignore = true)
  @Mapping(target = "completedInterviews", ignore = true)
  @Mapping(target = "conductedInterviews", ignore = true)
  @Mapping(target = "masteries", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract Specialization toEntity(SpecializationDto specializationDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mainMastery", ignore = true)
  @Mapping(target = "completedInterviews", ignore = true)
  @Mapping(target = "conductedInterviews", ignore = true)
  @Mapping(target = "masteries", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract Specialization updateEntity(SpecializationDto specializationDto,
      @MappingTarget Specialization specialization);

  @Mapping(target = "mainMasteryName", source = "mainMastery.level",
      qualifiedByName = "getMainMasteryName")
  public abstract SpecializationDto toDto(Specialization specialization);

  /**
   * Retrieves the name of the main mastery level associated with a given numeric level.
   *
   * @param level the numeric level to search for
   * @return the name of the mastery level corresponding to the given numeric level, or "Unknown"
   */
  @Named("getMainMasteryName")
  public String getMainMasteryName(int level) {
    List<String> masteryLevels = JsonUtil.loadStringFromJson(DEFAULT_MASTERY_LEVELS_PATH);
    return IntStream.range(0, masteryLevels.size())
        .boxed()
        .collect(Collectors.toMap(i -> i + 1, masteryLevels::get))
        .getOrDefault(level, "Unknown");
  }
}
