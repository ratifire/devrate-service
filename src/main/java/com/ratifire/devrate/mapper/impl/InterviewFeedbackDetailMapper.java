package com.ratifire.devrate.mapper.impl;

import com.ratifire.devrate.dto.InterviewFeedbackDetailDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.interview.InterviewFeedbackDetail;
import com.ratifire.devrate.mapper.DataMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Class for mapping InterviewFeedbackDetail entities to FeedbackDetailDto.
 */
@Mapper(componentModel = "spring")
public abstract class InterviewFeedbackDetailMapper implements
    DataMapper<InterviewFeedbackDetailDto, InterviewFeedbackDetail> {

  protected ParticipantMapper participantMapper;
  protected SkillMapper skillMapper;

  @Autowired
  protected void setParticipantMapper(ParticipantMapper participantMapper) {
    this.participantMapper = participantMapper;
  }

  @Autowired
  protected void setSkillMapper(SkillMapper skillMapper) {
    this.skillMapper = skillMapper;
  }

  @Override
  @Mapping(source = "detail.startTime", target = "interviewStartTime")
  @Mapping(target = "participant", expression = "java(participantMapper.toDto(detail.getUser(), "
      + "detail.getParticipantRole()))")
  @Mapping(target = "skills", expression = "java(skillMapper.toSkillShortDtos(skills))")
  public abstract InterviewFeedbackDetailDto toDto(InterviewFeedbackDetail detail,
      List<Skill> skills);
}