package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.InterviewRequestDto;
import com.ratifire.devrate.dto.InterviewRequestViewDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.entity.interview.InterviewRequestTimeSlot;
import com.ratifire.devrate.enums.TimeSlotStatus;
import com.ratifire.devrate.exception.InterviewRequestDoesntExistException;
import com.ratifire.devrate.exception.InvalidInterviewRequestException;
import com.ratifire.devrate.mapper.impl.InterviewRequestMapper;
import com.ratifire.devrate.mapper.impl.InterviewRequestTimeSlotMapper;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import com.ratifire.devrate.repository.interview.InterviewRequestTimeSlotRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
  private final InterviewRequestTimeSlotRepository interviewRequestTimeSlotRepository;
  private final InterviewRequestTimeSlotMapper interviewRequestTimeSlotMapper;

  public Optional<Long> findMasteryId(long id) {
    return repository.findMasteryIdById(id);
  }

  public List<InterviewRequest> findByIds(List<Long> ids) {
    return repository.findByIds(ids);
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
        .comment(request.getComment())
        .languageCode(request.getLanguageCode())
        .timeSlots(interviewRequestTimeSlotMapper.toDto(request.getTimeSlots()))
        .build();
  }

  /**
   * Creates an interview request for the specified user and send it to matcher-service.
   *
   * @param requestDto the DTO containing the interview request details
   */
  @Transactional
  public void create(InterviewRequestDto requestDto) {
    validateRequest(requestDto);

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
    mapper.updateEntity(requestDto, interviewRequest);

    updateTimeSlots(interviewRequest, requestDto.getTimeSlots());

    repository.save(interviewRequest);

    matcherServiceQueueSender.update(interviewRequest);
  }

  private void updateTimeSlots(InterviewRequest interviewRequest, List<ZonedDateTime> timeSlots) {
    if (CollectionUtils.isEmpty(timeSlots)) {
      throw new InvalidInterviewRequestException("Time slots is a required parameters.");
    }

    Set<ZonedDateTime> updatedDateTime = new HashSet<>(timeSlots);

    interviewRequest.getTimeSlots().removeIf(slot -> !updatedDateTime.contains(slot.getDateTime()));

    List<InterviewRequestTimeSlot> existingTimeSlots = interviewRequest.getTimeSlots();
    Set<ZonedDateTime> existingDateTime = existingTimeSlots.stream()
        .map(InterviewRequestTimeSlot::getDateTime)
        .collect(Collectors.toSet());

    List<InterviewRequestTimeSlot> timeSlotsToAdd = updatedDateTime.stream()
        .filter(dateTime -> !existingDateTime.contains(dateTime))
        .map(dateTime -> InterviewRequestTimeSlot.builder()
            .interviewRequest(interviewRequest)
            .dateTime(dateTime)
            .status(TimeSlotStatus.PENDING)
            .build())
        .toList();

    validateRequest(interviewRequest.getDesiredInterview(), existingTimeSlots, timeSlotsToAdd);

    interviewRequestTimeSlotRepository.deleteByInterviewRequestAndDateTimeNotIn(interviewRequest,
        updatedDateTime);

    if (!CollectionUtils.isEmpty(timeSlotsToAdd)) {
      interviewRequest.getTimeSlots().addAll(timeSlotsToAdd);
      interviewRequestTimeSlotRepository.saveAll(timeSlotsToAdd);
    }
  }

  private void validateRequest(int desiredInterview,
      List<InterviewRequestTimeSlot> existingTimeSlots,
      List<InterviewRequestTimeSlot> timeSlotsToAdd) {
    long countOfPendingTimeSlot = Stream.concat(existingTimeSlots.stream(), timeSlotsToAdd.stream())
        .filter(t -> TimeSlotStatus.PENDING == t.getStatus()).count();
    if (countOfPendingTimeSlot < desiredInterview) {
      throw new InvalidInterviewRequestException("Count of pending dates \""
          + countOfPendingTimeSlot + "\" must be greater than or equal to the desired number \""
          + desiredInterview + "\" of interviews.");
    }
  }

  private void validateRequest(InterviewRequestDto requestDto) {
    List<ZonedDateTime> timeSlots = requestDto.getTimeSlots();
    if (CollectionUtils.isEmpty(timeSlots)) {
      throw new InvalidInterviewRequestException("Time slots is a required parameters.");
    }
    if (timeSlots.size() < requestDto.getDesiredInterview()) {
      throw new InvalidInterviewRequestException("Count of pending dates \"" + timeSlots.size()
          + "\" must be greater than or equal to the desired number \""
          + requestDto.getDesiredInterview() + "\" of interviews.");
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

    interviewRequestTimeSlotRepository.updateTimeSlotStatus(scheduledInterviewRequests,
        scheduledDate, TimeSlotStatus.BOOKED);
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
