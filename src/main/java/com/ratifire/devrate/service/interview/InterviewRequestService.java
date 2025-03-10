package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewRequestViewDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.entity.interview.InterviewRequestTimeSlot;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.TimeSlotStatus;
import com.ratifire.devrate.exception.InterviewRequestDoesntExistException;
import com.ratifire.devrate.exception.InterviewRequestNotFoundException;
import com.ratifire.devrate.exception.InvalidInterviewRequestException;
import com.ratifire.devrate.mapper.impl.InterviewRequestMapper;
import com.ratifire.devrate.mapper.impl.InterviewRequestTimeSlotMapper;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import com.ratifire.devrate.repository.interview.InterviewRequestTimeSlotRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Service for handling Interview Requests.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewRequestService {

  private final InterviewRequestRepository repository;
  private final InterviewRequestMapper mapper;
  private final MatcherServiceQueueSender matcherServiceQueueSender;
  private final UserContextProvider userContextProvider;
  private final InterviewRequestTimeSlotRepository timeSlotRepository;
  private final InterviewRequestTimeSlotMapper timeSlotMapper;

  public Optional<Long> findMasteryId(long id) {
    return repository.findMasteryIdById(id);
  }

  public List<InterviewRequest> findByIds(List<Long> ids) {
    return repository.findAllById(ids);
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
        .matchedInterview(request.getMatchedInterview())
        .comment(request.getComment())
        .languageCode(request.getLanguageCode())
        .timeSlots(timeSlotMapper.toDto(request.getTimeSlots()))
        .build();
  }

  /**
   * Creates an interview request for the specified user and send it to matcher-service.
   *
   * @param requestDto the DTO containing the interview request details
   */
  @Transactional
  public void create(InterviewRequestDto requestDto) {
    validateRequest(requestDto.getTimeSlots(), requestDto.getDesiredInterview());

    long userId = userContextProvider.getAuthenticatedUserId();

    List<InterviewRequestTimeSlot> interviewRequestTimeSlots =
        createTimeSlots(requestDto.getTimeSlots());

    InterviewRequest interviewRequest = mapper.toEntity(requestDto);
    interviewRequest.setUser(User.builder().id(userId).build());
    interviewRequest.setBlackList(new HashSet<>());

    interviewRequestTimeSlots.forEach(slot -> slot.setInterviewRequest(interviewRequest));

    interviewRequest.setTimeSlots(interviewRequestTimeSlots);

    repository.save(interviewRequest);

    matcherServiceQueueSender.create(interviewRequest);
  }

  private List<InterviewRequestTimeSlot> createTimeSlots(List<ZonedDateTime> timesSlots) {
    return timesSlots.stream()
        .map(timesSlot -> InterviewRequestTimeSlot.builder()
            .dateTime(timesSlot)
            .status(TimeSlotStatus.PENDING)
            .build())
        .toList();
  }

  /**
   * Updates the interview request for the specified user based on the provided request DTO.
   *
   * @param requestDto the interview request details
   */
  @Transactional
  public void update(long id, InterviewRequestDto requestDto) {
    long userId = userContextProvider.getAuthenticatedUserId();

    InterviewRequest interviewRequest = repository.findByIdAndUser_Id(id, userId)
        .orElseThrow(() -> new InterviewRequestDoesntExistException(id, userId));

    List<ZonedDateTime> pendingDateTime = interviewRequest.getTimeSlots().stream()
        .filter(dateTime -> TimeSlotStatus.PENDING == dateTime.getStatus())
        .map(InterviewRequestTimeSlot::getDateTime)
        .toList();

    validateRequest(pendingDateTime, requestDto.getDesiredInterview());

    mapper.updateEntity(requestDto, interviewRequest);
    repository.save(interviewRequest);

    matcherServiceQueueSender.update(interviewRequest);
  }

  private void validateRequest(List<ZonedDateTime> timeSlots, int desiredInterview) {
    if (CollectionUtils.isEmpty(timeSlots)) {
      throw new InvalidInterviewRequestException("Time slots is a required parameters.");
    }
    if (timeSlots.size() < desiredInterview) {
      throw new InvalidInterviewRequestException("Count of pending dates \"" + timeSlots.size()
          + "\" must be greater than or equal to the desired number \""
          + desiredInterview + "\" of interviews.");
    }
  }

  /**
   * Updates the time slots for the specified interview requests.
   *
   * @param scheduledInterviewRequests the list of interview requests
   * @param scheduledDate              the date to assign to the requests
   */
  @Transactional
  public void markTimeSlotsAsBooked(List<InterviewRequest> scheduledInterviewRequests,
      ZonedDateTime scheduledDate) {
    if (scheduledInterviewRequests.isEmpty()) {
      return;
    }

    timeSlotRepository.updateTimeSlotStatus(scheduledInterviewRequests, scheduledDate,
        TimeSlotStatus.BOOKED);
  }

  /**
   * Increments the matched interview count for each InterviewRequest in the given list.
   *
   * @param requests the list of InterviewRequest entities to be updated
   */
  public void incrementMatchedInterviewCount(List<InterviewRequest> requests) {
    if (CollectionUtils.isEmpty(requests)) {
      throw new IllegalArgumentException("Request list cannot be null or empty");
    }
    requests.forEach(request -> request.setMatchedInterview(request.getMatchedInterview() + 1));
    repository.saveAll(requests);
  }

  /**
   * Marks rejected interview time slots as pending.
   *
   * @param interviews the list of rejected interviews to process
   */
  public void markRejectedInterviewTimeSlotsAsPending(List<Interview> interviews) {
    if (CollectionUtils.isEmpty(interviews)) {
      log.warn("No interviews provided for rejection handling");
      return;
    }

    Map<Long, InterviewRequestRole> roleByMastery = interviews.stream()
        .collect(Collectors.toMap(Interview::getMasteryId, Interview::getRole, (r1, r2) -> r1));

    List<Long> masteryIds = new ArrayList<>(roleByMastery.keySet());
    List<InterviewRequestRole> roles = new ArrayList<>(roleByMastery.values());

    // TODO: need improvements, List<InterviewRequest> from the repository should return interview
    //  request that already have paired (masteryId + role) like a key. And after that we don't make
    //  code below.
    // Perform a single query to fetch all requests (may return some incorrect combinations)
    List<InterviewRequest> allRequests = repository.findByMastery_IdInAndRoleIn(masteryIds, roles);

    // Filter out only the correct masteryId + role pairs
    List<InterviewRequest> interviewRequestsToUpdate = allRequests.stream()
        .filter(req -> roleByMastery.containsKey(req.getMastery().getId())
            && roleByMastery.get(req.getMastery().getId()).equals(req.getRole()))
        .toList();

    if (interviewRequestsToUpdate.isEmpty()) {
      log.warn("Rejected interview doesn't have available interview request");
      return;
    }

    ZonedDateTime interviewStartTime = interviews.getFirst().getStartTime();
    timeSlotRepository.updateTimeSlotStatus(interviewRequestsToUpdate, interviewStartTime,
        TimeSlotStatus.PENDING);

    // Fetch the latest updated InterviewRequests from DB
    List<Long> requestIds = interviewRequestsToUpdate.stream()
        .map(InterviewRequest::getId)
        .toList();
    List<InterviewRequest> updatedRequests = repository.findAllById(requestIds);

    updatedRequests.forEach(matcherServiceQueueSender::update);
  }

  /**
   * Add time slots to the specific interview request.
   *
   * @param id        the interview request ID
   * @param dateTimes list of the time slots that should be added
   */
  public void addTimeSlots(long id, List<ZonedDateTime> dateTimes) {
    InterviewRequest interviewRequest = repository.findById(id)
        .orElseThrow(() -> new InterviewRequestNotFoundException(id));

    List<InterviewRequestTimeSlot> timeSlotsToAdd = dateTimes.stream()
        .map(dateTime -> InterviewRequestTimeSlot.builder()
            .interviewRequest(interviewRequest)
            .dateTime(dateTime)
            .status(TimeSlotStatus.PENDING)
            .build())
        .toList();

    if (!CollectionUtils.isEmpty(timeSlotsToAdd)) {
      interviewRequest.getTimeSlots().addAll(timeSlotsToAdd);
      repository.save(interviewRequest);
    }

    matcherServiceQueueSender.update(interviewRequest);
  }

  /**
   * Delete time slots to the specific interview request.
   *
   * @param id        the interview request ID
   * @param dateTimes list of the time slots that should be deleted
   */
  public void deleteTimeSlots(long id, List<ZonedDateTime> dateTimes) {
    InterviewRequest interviewRequest = repository.findById(id)
        .orElseThrow(() -> new InterviewRequestNotFoundException(id));

    List<InterviewRequestTimeSlot> timeSlotsToDelete = interviewRequest.getTimeSlots().stream()
        .filter(timeSlot -> dateTimes.contains(timeSlot.getDateTime()))
        .toList();

    if (!CollectionUtils.isEmpty(timeSlotsToDelete)) {
      interviewRequest.getTimeSlots().removeAll(timeSlotsToDelete);
      repository.save(interviewRequest);
    }

    matcherServiceQueueSender.update(interviewRequest);
  }

  /**
   * Deletes an interview request by its ID and user ID.
   *
   * @param id the ID of the interview request to be deleted
   */
  @Transactional
  public void delete(long id) {
    long userId = userContextProvider.getAuthenticatedUserId();
    repository.deleteByIdAndUser_Id(id, userId);

    matcherServiceQueueSender.delete(id);
  }
}
