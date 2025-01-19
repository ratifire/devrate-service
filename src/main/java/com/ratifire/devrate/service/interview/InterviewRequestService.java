package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewRequestViewDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.exception.InterviewRequestDoesntExistException;
import com.ratifire.devrate.exception.InvalidInterviewRequestException;
import com.ratifire.devrate.mapper.impl.InterviewRequestMapper;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling Interview Requests.
 */
@Service
@RequiredArgsConstructor
public class InterviewRequestService {

  private final InterviewRequestRepository repository;
  private final InterviewRequestMapper mapper;
  private final MatcherServiceQueueSender matcherServiceQueueSender;
  private final UserContextProvider userContextProvider;

  public Optional<Long> findMasteryId(long id) {
    return repository.findMasteryIdById(id);
  }

  public List<InterviewRequest> findByIds(List<Long> ids) {
    return repository.findByIds(ids);
  }

  /**
   * Updates the assigned dates for the specified interview requests by adding a new assigned date.
   *
   * @param interviewerRequestId the ID of the interviewer's request
   * @param candidateRequestId   the ID of the candidate's request
   * @param assignedDate         the date to assign to the requests
   */
  @Transactional
  public void updateAssignedDates(long interviewerRequestId,
      long candidateRequestId, ZonedDateTime assignedDate) {
    List<InterviewRequest> scheduledInterviewRequests = findByIds(
        List.of(interviewerRequestId, candidateRequestId));

    if (scheduledInterviewRequests.isEmpty()) {
      return;
    }

    scheduledInterviewRequests.forEach(request -> request.getAssignedDates().add(assignedDate));
    repository.saveAll(scheduledInterviewRequests);
  }

  /**
   * Retrieves all interview requests for the authenticated user.
   *
   * @return a list of {@link InterviewRequestViewDto} containing the details.
   */
  public List<InterviewRequestViewDto> getAll() {
    long userId = userContextProvider.getAuthenticatedUserId();
    return repository.findAllByUser_Id(userId)
        .stream()
        .map(this::convertToInterviewRequestViewDto)
        .toList();
  }

  /**
   * Retrieves a list of interview requests by mastery ID for the authenticated user.
   *
   * @param masteryId the ID of the mastery to fetch interview requests for.
   * @return a list of {@link InterviewRequestViewDto} containing the details.
   */
  public List<InterviewRequestViewDto> getByMasteryId(long masteryId) {
    long userId = userContextProvider.getAuthenticatedUserId();

    return repository.findAllByMastery_IdAndUser_Id(masteryId, userId)
        .stream()
        .map(this::convertToInterviewRequestViewDto)
        .toList();
  }

  /**
   * Converts an InterviewRequest entity to an InterviewRequestViewDto.
   *
   * @param request the InterviewRequest entity to convert.
   * @return the converted {@link InterviewRequestViewDto}.
   */
  private InterviewRequestViewDto convertToInterviewRequestViewDto(InterviewRequest request) {
    return InterviewRequestViewDto.builder()
        .id(request.getId())
        .role(request.getRole())
        .desiredInterview(request.getDesiredInterview())
        .availableDates(request.getAvailableDates())
        .assignedDates(request.getAssignedDates())
        .build();
  }

  /**
   * Creates an interview request for the specified user and send it to matcher-service.
   *
   * @param requestDto the DTO containing the interview request details
   */
  public void create(InterviewRequestDto requestDto) {
    validateRequest(requestDto);

    long userId = userContextProvider.getAuthenticatedUserId();

    InterviewRequest interviewRequest = mapper.toEntity(requestDto);
    interviewRequest.setUser(User.builder().id(userId).build());
    interviewRequest.setBlackList(new HashSet<>());
    repository.save(interviewRequest);

    matcherServiceQueueSender.create(interviewRequest);
  }

  /**
   * Updates the interview request for the specified user based on the provided request DTO.
   *
   * @param requestDto the interview request details
   */
  public void update(long id, InterviewRequestDto requestDto) {
    validateRequest(requestDto);

    long userId = userContextProvider.getAuthenticatedUserId();

    InterviewRequest interviewRequest = repository.findByIdAndUser_Id(id, userId)
        .orElseThrow(() -> new InterviewRequestDoesntExistException(id, userId));
    mapper.updateEntity(requestDto, interviewRequest);
    repository.save(interviewRequest);

    matcherServiceQueueSender.update(interviewRequest);
  }

  /**
   * Validates the interview request details.
   *
   * @param requestDto the interview request to validate
   */
  private void validateRequest(InterviewRequestDto requestDto) {
    Optional.ofNullable(requestDto.getAvailableDates())
        .filter(dates -> dates.size() >= requestDto.getDesiredInterview())
        .orElseThrow(() -> new InvalidInterviewRequestException(
            "Available dates must be greater than or equal to the desired number of interviews."));
  }

  /**
   * Deletes an interview request by its ID and user ID.
   *
   * @param id the ID of the interview request to be deleted
   */
  public void delete(long id) {
    long userId = userContextProvider.getAuthenticatedUserId();
    repository.deleteByIdAndUser_Id(id, userId);
  }
}
