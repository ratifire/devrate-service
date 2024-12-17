package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.ParticipantInterviewRequestDto;
import com.ratifire.devrate.entity.Skill;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.entity.interview.InterviewRequestV2;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.exception.InterviewRequestDoesntExistException;
import com.ratifire.devrate.mapper.impl.InterviewRequestMapperV2;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import com.ratifire.devrate.repository.interview.InterviewRequestRepositoryV2;
import com.ratifire.devrate.sender.InterviewRequestSender;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling Interview Requests.
 */
@Service
@RequiredArgsConstructor
public class InterviewRequestService {

  private final InterviewRequestRepository repository;
  private final InterviewRequestRepositoryV2 repositoryV2;
  private final InterviewRequestMapperV2 mapper;
  private final InterviewRequestSender interviewRequestSender;

  /**
   * Updates the interview request for the specified user based on the provided request DTO.
   *
   * @param userId     the user's ID
   * @param requestDto the interview request details
   */
  public void update(long userId, InterviewRequestDto requestDto) {
    InterviewRequestV2 interviewRequest =
        findByUserIdRoleMasteryId(userId, requestDto.getRole(), requestDto.getMasteryId());
    mapper.updateEntity(requestDto, interviewRequest);
    repositoryV2.save(interviewRequest);

    // TODO: should be send message to matched-service for update ParticipantInterviewRequestDto
  }

  /**
   * Retrieves the interview request for the specified user, role and mastery id.
   *
   * @param userId    the user's ID
   * @param role      the role of the interview request
   * @param masteryId the mastery id of the interview request
   * @return the interview request as InterviewRequestDto
   */
  public InterviewRequestDto find(long userId, InterviewRequestRole role,
      long masteryId) {
    InterviewRequestV2 interviewRequest = findByUserIdRoleMasteryId(userId, role, masteryId);
    return mapper.toDto(interviewRequest);
  }

  /**
   * Finds an interview request for the given userId, role and mastery. Throw error if not found.
   *
   * @param userId    the ID of the user associated with the interview request
   * @param role      the role of the interview request
   * @param masteryId the mastery id of the interview request
   * @return the found InterviewRequest or an empty InterviewRequest if not found
   */
  private InterviewRequestV2 findByUserIdRoleMasteryId(long userId, InterviewRequestRole role,
      long masteryId) {
    return repositoryV2.findByUserIdAndRoleAndMastery_Id(userId, role, masteryId)
        .orElseThrow(
            () -> new InterviewRequestDoesntExistException(userId, role.name(), masteryId));
  }

  /**
   * Handles the actions required after an interview is rejected.
   *
   * @param activeRequest The active interview request.
   * @param rejectedRequest The rejected interview request.
   */
  public void handleRejectedInterview(InterviewRequest activeRequest,
      InterviewRequest rejectedRequest) {
    // TODO: implement logic for send message to matched-service
    activeRequest.setActive(true);
    rejectedRequest.setActive(true);
    repository.save(activeRequest);
    repository.save(rejectedRequest);
  }

  /**
   * Deletes interview requests by their IDs.
   *
   * @param ids the list of interview request IDs to be deleted.
   */
  public void deleteBulk(List<Long> ids) {
    repositoryV2.deleteAllById(ids);
  }

  /**
   * Deletes an interview request by its ID if the request is active.
   *
   * @param id the ID of the interview request to be deleted
   */
  public void delete(long id) {
    repositoryV2.deleteById(id);
  }

  /**
   * Creates an interview request for the specified user and attempts to match it with an existing
   * request.
   *
   * @param userId     the ID of the user creating the interview request
   * @param requestDto the DTO containing the interview request details
   */
  public void create(InterviewRequestDto requestDto, long userId) {
    InterviewRequestV2 interviewRequest = mapper.toEntity(requestDto);
    interviewRequest.setUser(User.builder().id(userId).build());
    repositoryV2.save(interviewRequest);

    String specializationName = interviewRequest.getMastery().getSpecialization().getName();
    int masteryLevel = interviewRequest.getMastery().getLevel();

    List<Skill> skills = interviewRequest.getMastery().getSkills();
    Set<String> hardSkills = skills.stream()
        .filter(skill -> skill.getType() == SkillType.HARD_SKILL).map(Skill::getName)
        .collect(Collectors.toSet());
    Set<String> softSkills = skills.stream()
        .filter(skill -> skill.getType() == SkillType.SOFT_SKILL).map(Skill::getName)
        .collect(Collectors.toSet());

    ParticipantInterviewRequestDto participantInterviewRequestDto =
        ParticipantInterviewRequestDto.builder()
            .participantId(userId)
            .desiredInterview(requestDto.getDesiredInterview())
            .role(interviewRequest.getRole())
            .specialization(specializationName)
            .masteryLevel(masteryLevel)
            .hardSkills(hardSkills)
            .softSkills(softSkills)
            .dates(interviewRequest.getAvailableDates().stream()
                .map(zdt -> Date.from(zdt.toInstant()))
                .collect(Collectors.toSet())).averageMark(interviewRequest.getAverageMark())
            .blackList(new HashSet<>())
            .build();

    interviewRequestSender.send(participantInterviewRequestDto);
  }
}
