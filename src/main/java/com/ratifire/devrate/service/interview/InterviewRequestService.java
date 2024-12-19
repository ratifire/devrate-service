package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewRequestDoesntExistException;
import com.ratifire.devrate.mapper.impl.InterviewRequestMapper;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling Interview Requests.
 */
@Service
@RequiredArgsConstructor
public class InterviewRequestService {

  private final InterviewRequestRepository repository;
  private final InterviewRequestMapper mapper;
  private final MatcherServiceOrchestrator matcherServiceOrchestrator;

  /**
   * Updates the interview request for the specified user based on the provided request DTO.
   *
   * @param userId     the user's ID
   * @param requestDto the interview request details
   */
  public void update(long userId, InterviewRequestDto requestDto) {
    InterviewRequest interviewRequest =
        findByUserIdRoleMasteryId(userId, requestDto.getRole(), requestDto.getMasteryId());
    mapper.updateEntity(requestDto, interviewRequest);
    repository.save(interviewRequest);

    matcherServiceOrchestrator.update(interviewRequest);
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
    InterviewRequest interviewRequest = findByUserIdRoleMasteryId(userId, role, masteryId);
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
  private InterviewRequest findByUserIdRoleMasteryId(long userId, InterviewRequestRole role,
      long masteryId) {
    return repository.findByUserIdAndRoleAndMastery_Id(userId, role, masteryId)
        .orElseThrow(
            () -> new InterviewRequestDoesntExistException(userId, role.name(), masteryId));
  }

  /**
   * Deletes interview requests by their IDs.
   *
   * @param ids the list of interview request IDs to be deleted.
   */
  public void deleteBulk(List<Long> ids) {
    repository.deleteAllById(ids);
  }

  /**
   * Deletes an interview request by its ID if the request is active.
   *
   * @param id the ID of the interview request to be deleted
   */
  public void delete(long id) {
    repository.deleteById(id);
  }

  /**
   * Creates an interview request for the specified user and send it to matcher-service.
   *
   * @param userId     the ID of the user creating the interview request
   * @param requestDto the DTO containing the interview request details
   */
  public void create(InterviewRequestDto requestDto, long userId) {
    InterviewRequest interviewRequest = mapper.toEntity(requestDto);
    interviewRequest.setUser(User.builder().id(userId).build());
    interviewRequest.setBlackList(new HashSet<>());
    repository.save(interviewRequest);

    matcherServiceOrchestrator.sendToQueue(interviewRequest);
  }
}
