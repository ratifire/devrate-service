package com.ratifire.devrate.service.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.CANDIDATE;
import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.dto.ClosestEventDto;
import com.ratifire.devrate.dto.InterviewDto;
import com.ratifire.devrate.dto.InterviewEventDto;
import com.ratifire.devrate.dto.InterviewFeedbackDetailDto;
import com.ratifire.devrate.dto.InterviewsOverallStatusDto;
import com.ratifire.devrate.dto.PairedParticipantDto;
import com.ratifire.devrate.dto.ParticipantDto;
import com.ratifire.devrate.dto.SkillShortDto;
import com.ratifire.devrate.dto.projection.InterviewUserMasteryProjection;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.ConsentStatus;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.InterviewStatusIndicator;
import com.ratifire.devrate.enums.MasteryLevel;
import com.ratifire.devrate.enums.SkillType;
import com.ratifire.devrate.exception.InterviewNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.EventService;
import com.ratifire.devrate.service.MasteryService;
import com.ratifire.devrate.service.MeetingService;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.SnsPublisherService;
import com.ratifire.devrate.service.UserService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Service for handling Interview operations.
 */
@Service
@RequiredArgsConstructor
public class InterviewService {

  private static final int INTERVIEW_DURATION = 1;    // hours

  private final MeetingService meetingService;
  private final InterviewRequestService interviewRequestService;
  private final EventService eventService;
  private final UserService userService;
  private final MasteryService masteryService;
  private final EmailService emailService;
  private final NotificationService notificationService;
  private final InterviewRepository interviewRepository;
  private final UserContextProvider userContextProvider;
  private final DataMapper<InterviewDto, Interview> mapper;
  private final SnsPublisherService snsPublisherService;

  /**
   * Retrieves a single visible interview by id for the auth user.
   *
   * @return an InterviewDto object by id
   */
  public InterviewDto findVisibleInterviewById(long interviewId) {
    long currentUserId = userContextProvider.getAuthenticatedUserId();
    Interview interview = interviewRepository
        .findByIdAndUserIdAndIsVisibleTrue(interviewId, currentUserId)
        .orElseThrow(() -> new InterviewNotFoundException(interviewId));
    return constructInterviewDto(interview);
  }

  public Optional<Interview> findByIdAndUserId(long id) {
    long currentUserId = userContextProvider.getAuthenticatedUserId();
    return interviewRepository.findByIdAndUserId(id, currentUserId);
  }


  /**
   * Retrieves a all interviews associated with the currently authenticated user.
   *
   * @return an InterviewDto objects representing the user's interviews
   */
  public Page<InterviewDto> findAll(int page, int size) {
    long userId = userContextProvider.getAuthenticatedUserId();
    Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
    Page<Interview> interviews = interviewRepository.findByUserIdAndIsVisibleTrue(userId, pageable);

    return interviews.map(this::constructInterviewDto);
  }

  private InterviewDto constructInterviewDto(Interview interview) {
    Mastery mastery = masteryService.getMasteryById(interview.getMasteryId());
    InterviewUserMasteryProjection projection =
        interviewRepository.findUserIdAndMasterIdByEventIdAndUserIdNot(
            interview.getEventId(),
            interview.getUserId());

    Long hostId = projection.getUserId();
    Long hostMasterId = projection.getMasteryId();
    if (ObjectUtils.isEmpty(hostId) || ObjectUtils.isEmpty(hostMasterId)) {
      throw new IllegalStateException("Not enough providing interview data");
    }

    User host = userService.findById(hostId);
    return mapper.toDto(
        interview,
        mastery.getLevel(),
        mastery.getSpecialization().getName(),
        hostId,
        host.getFirstName(),
        host.getLastName(),
        hostMasterId);
  }

  /**
   * Retrieves the details of an interview event for the given event ID.
   *
   * @param id the ID of the event for which interview details are to be retrieved
   * @return an InterviewEventDto containing the details of the interview event
   */
  public InterviewEventDto getInterviewEventDetails(long id) {
    long currentUserId = userContextProvider.getAuthenticatedUserId();
    List<Interview> interviews = interviewRepository.findByEventId(id);
    Map<Long, User> usersById = getUsersByInterviews(interviews);
    Map<Long, Mastery> masterysById = getMasteriesByInterviews(interviews);

    Map<Long, ParticipantDto> participantsById = interviews.stream()
        .map(interview -> buildParticipant(usersById.get(interview.getUserId()), interview,
            masterysById))
        .collect(Collectors.toMap(ParticipantDto::getId, participant -> participant));

    ParticipantDto current = participantsById.get(currentUserId);
    ParticipantDto counterpart = participantsById.values().stream()
        .filter(participant -> participant.getId() != currentUserId)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Counterpart participant not found for id: " + id));

    return InterviewEventDto.builder()
        .currentUser(current)
        .counterpartUser(counterpart)
        .build();
  }

  /**
   * Retrieves a list of events for a given user that start from a specified date and time in UTC.
   *
   * @param from the starting date and time from which events should be retrieved
   * @return a list of {@link ClosestEventDto} objects representing the events starting from
   */
  public List<ClosestEventDto> findUpcomingEvents(ZonedDateTime from) {
    long authUserId = userContextProvider.getAuthenticatedUserId();
    User authUser = userService.findById(authUserId);

    List<Event> upcomingEvents = authUser.getEvents().stream()
        .filter(event -> !event.getStartTime().isBefore(from.withZoneSameInstant(ZoneId.of("UTC"))))
        .toList();

    if (upcomingEvents.isEmpty()) {
      return List.of();
    }

    // Find and group interviews by event id
    List<Long> eventIds = upcomingEvents.stream().map(Event::getId).toList();
    List<Interview> allInterviews = interviewRepository.findByEventIdIn(eventIds);
    Map<Long, List<Interview>> interviewsByEventId = allInterviews.stream()
        .collect(Collectors.groupingBy(Interview::getEventId));

    List<Long> opponentUserIds = allInterviews.stream()
        .map(Interview::getUserId)
        .filter(userId -> userId != authUserId)
        .toList();

    Map<Long, User> opponentById = userService.findByIds(opponentUserIds).stream()
        .collect(Collectors.toMap(User::getId, user -> user));

    return upcomingEvents.stream()
        .map(event -> createClosestEventDto(event, interviewsByEventId.get(event.getId()),
            authUserId, opponentById))
        .filter(Objects::nonNull)
        .toList();
  }

  private ClosestEventDto createClosestEventDto(Event event, List<Interview> eventInterviews,
      long authUserId, Map<Long, User> opponentById) {
    if (CollectionUtils.isEmpty(eventInterviews)) {
      return null;
    }

    Interview authInterview = null;
    Interview opponentInterview = null;
    for (Interview interview : eventInterviews) {
      if (interview.getUserId() == authUserId) {
        authInterview = interview;
      } else {
        opponentInterview = interview;
      }
    }

    if (authInterview == null || opponentInterview == null) {
      return null;
    }

    User opponent = opponentById.get(opponentInterview.getUserId());
    if (opponent == null) {
      return null;
    }

    return ClosestEventDto.builder()
        .id(event.getId())
        .type(event.getType())
        .startTime(event.getStartTime())
        .hostId(opponent.getId())
        .hostName(opponent.getFirstName())
        .hostSurname(opponent.getLastName())
        .title(event.getTitle())
        .interviewId(authInterview.getId())
        .role(authInterview.getRole())
        .roomUrl(event.getRoomLink())
        .build();
  }

  /**
   * Creates interviews and an associated event based on the given paired participant data.
   *
   * @param matchedUsers Data transfer object containing the details of paired participant.
   */
  @Transactional
  public void create(PairedParticipantDto matchedUsers) {
    long interviewerId = matchedUsers.getInterviewerId();
    long candidateId = matchedUsers.getCandidateId();
    long interviewerRequestId = matchedUsers.getInterviewerParticipantId();
    long candidateRequestId = matchedUsers.getCandidateParticipantId();
    ZonedDateTime date = matchedUsers.getDate();

    String joinUrl = meetingService.createMeeting();

    Mastery mastery =
        masteryService.getMasteryById(interviewRequestService.findMasteryId(candidateRequestId)
            .orElseThrow(() -> new IllegalStateException(
                "Mastery ID not found for interview request with id: " + candidateRequestId)));

    String title = MasteryLevel.fromLevel(mastery.getLevel())
        + " " + mastery.getSpecialization().getName();
    Event event = eventService.buildEvent(candidateId, interviewerId, joinUrl, date, title);
    long eventId = eventService.save(event, List.of(interviewerId, candidateId));

    List<InterviewRequest> requests = interviewRequestService.findByIds(
        List.of(interviewerRequestId, candidateRequestId));

    String interviewerRequestComment = extractCommentForRole(requests, INTERVIEWER);
    String interviewerLanguageCode = extractLanguageCode(requests, INTERVIEWER);
    ConsentStatus interviewerConsentStatus = extractContentStatus(requests, INTERVIEWER);
    Interview interviewer = buildInterview(interviewerId, interviewerRequestId, eventId,
        INTERVIEWER, joinUrl, date, interviewerRequestComment, interviewerLanguageCode,
        interviewerConsentStatus);
    String candidateRequestComment = extractCommentForRole(requests, CANDIDATE);
    String candidateLanguageCode = extractLanguageCode(requests, CANDIDATE);
    ConsentStatus candidateConsentStatus = extractContentStatus(requests, CANDIDATE);
    Interview candidate = buildInterview(candidateId, candidateRequestId, eventId, CANDIDATE,
        joinUrl, date, candidateRequestComment, candidateLanguageCode, candidateConsentStatus);
    List<Interview> interviews = interviewRepository.saveAll(List.of(interviewer, candidate));

    // TODO: need refactoring related to the interview list that use only for getting interviewIds
    //  for each users

    interviewRequestService.updateTimeSlots(interviewerRequestId, candidateRequestId, date,
        interviews);
    interviewRequestService.incrementMatchedInterviewCount(requests);

    Map<Long, InterviewRequest> requestMap = requests.stream()
        .collect(Collectors.toMap(InterviewRequest::getId, request -> request));

    sendInterviewScheduledAlerts(
        requestMap.get(interviewerRequestId), requestMap.get(candidateRequestId), date, interviews);
  }

  private String extractCommentForRole(List<InterviewRequest> requests, InterviewRequestRole role) {
    return requests.stream()
        .filter(r -> r.getRole() == role)
        .map(InterviewRequest::getComment)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("");
  }

  private String extractLanguageCode(List<InterviewRequest> requests, InterviewRequestRole role) {
    return requests.stream()
        .filter(r -> r.getRole() == role)
        .map(InterviewRequest::getLanguageCode)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("");
  }

  private ConsentStatus extractContentStatus(List<InterviewRequest> requests,
      InterviewRequestRole role) {
    return requests.stream()
        .filter(r -> r.getRole() == role)
        .map(InterviewRequest::getConsentStatus)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(ConsentStatus.NOT_GIVEN);
  }

  /**
   * Saves the given Interview entity to the repository.
   *
   * @param interview the Interview entity to be saved
   */
  public void save(Interview interview) {
    interviewRepository.save(interview);
  }

  /**
   * Deletes a rejected interview by its ID and returns the deleted interview.
   *
   * @param id the ID of the interview to be deleted
   */
  @Transactional
  public void deleteRejected(long id) {
    List<Interview> interviews = interviewRepository.findInterviewPairById(id);

    interviewRequestService.markRejectedInterviewTimeSlotsAsPending(interviews);

    interviewRepository.deleteAll(interviews);
    eventService.delete(interviews.getFirst().getEventId());
    //TODO: add logic for deleting provider meeting room (if needed)

    long rejectorId = userContextProvider.getAuthenticatedUserId();
    long recipientId = interviews.stream()
        .map(Interview::getUserId)
        .filter(userId -> userId != rejectorId)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "Could not find other userId in the interview pair"));

    List<User> users = userService.findByIds(List.of(rejectorId, recipientId));
    Map<Long, User> userMap = users.stream()
        .collect(Collectors.toMap(User::getId, user -> user));

    notifyParticipants(
        userMap.get(recipientId),
        userMap.get(rejectorId),
        interviews);
  }

  /**
   * Deletes all interview records associated with the specified ID.
   *
   * @param id the identifier of the interview records to be deleted
   */
  public void delete(long id) {
    List<Interview> interviews = interviewRepository.findInterviewPairById(id);
    interviewRepository.deleteAll(interviews);
  }

  /**
   * Deletes interview records with the specified IDs.
   *
   * @param ids the identifiers of the interview records to be deleted
   */
  public void deleteByIds(List<Long> ids) {
    interviewRepository.deleteAllById(ids);
  }


  /**
   * Retrieves detailed feedback information for an interview based on the given ID.
   *
   * @param id the ID of the interview for which feedback details are to be retrieved
   * @return an InterviewFeedbackDetailDto containing the feedback details
   */
  public InterviewFeedbackDetailDto getFeedbackDetail(long id) {
    Interview oppositeInterview = findOppositeInterview(id)
        .orElseThrow(() -> new IllegalStateException("Opposite interview not found for id: " + id));

    Mastery mastery = masteryService.getMasteryById(oppositeInterview.getMasteryId());
    User user = userService.findById(oppositeInterview.getUserId());

    List<SkillShortDto> skills = mastery.getSkills().stream()
        .filter(skill -> oppositeInterview.getRole() != InterviewRequestRole.INTERVIEWER
            || skill.getType() == SkillType.SOFT_SKILL)
        .map(skill -> new SkillShortDto(skill.getId(), skill.getName(), skill.getType()))
        .toList();

    ParticipantDto participantDto = ParticipantDto.builder()
        .id(user.getId())
        .name(user.getFirstName())
        .surname(user.getLastName())
        .masteryLevel(mastery.getLevel())
        .specializationName(mastery.getSpecialization().getName())
        .role(oppositeInterview.getRole())
        .build();

    return InterviewFeedbackDetailDto.builder()
        .interviewStartTime(oppositeInterview.getStartTime())
        .skills(skills)
        .participant(participantDto)
        .build();
  }

  /**
   * Finds the opposite interview for the given interview ID.
   *
   * @param id the ID of the interview for which the opposite interview is to be found
   * @return an Optional containing the opposite Interview
   */
  public Optional<Interview> findOppositeInterview(long id) {
    return interviewRepository.findOppositeInterview(id);
  }

  /**
   * Builds an Interview object using the provided parameters.
   *
   * @return the built Interview object
   */
  private Interview buildInterview(long userId, long requestId, long eventId,
      InterviewRequestRole role, String roomUrl, ZonedDateTime date, String requestComment,
      String languageCode, ConsentStatus consentStatus) {

    long masteryId = interviewRequestService.findMasteryId(requestId)
        .orElseThrow(() -> new IllegalStateException(
            "Mastery ID not found for interview request with id: " + requestId));

    return Interview.builder()
        .userId(userId)
        .masteryId(masteryId)
        .eventId(eventId)
        .role(role)
        .consentStatus(consentStatus)
        .roomUrl(roomUrl)
        .startTime(date)
        .languageCode(languageCode)
        .requestComment(requestComment)
        .isVisible(true)
        .build();
  }

  /**
   * Builds a ParticipantDto object based on the given User, Interview, and Mastery data.
   *
   * @return a ParticipantDto containing participant details
   */
  private ParticipantDto buildParticipant(User user, Interview interview,
      Map<Long, Mastery> masterysById) {
    Mastery mastery = masterysById.get(interview.getMasteryId());
    return ParticipantDto.builder()
        .id(user.getId())
        .name(user.getFirstName())
        .surname(user.getLastName())
        .masteryLevel(mastery.getLevel())
        .specializationName(mastery.getSpecialization().getName())
        .role(interview.getRole())
        .build();
  }

  /**
   * Retrieves a map of User objects indexed by their IDs, based on a list of interviews.
   *
   * @param interviews the list of Interview objects
   * @return a map where the key is the user ID and the value is the corresponding User object
   */
  private Map<Long, User> getUsersByInterviews(List<Interview> interviews) {
    List<Long> userIds = interviews.stream()
        .map(Interview::getUserId)
        .toList();
    return userService.findByIds(userIds).stream()
        .collect(Collectors.toMap(User::getId, user -> user));
  }

  /**
   * Retrieves a map of Mastery objects indexed by their IDs, based on a list of interviews.
   *
   * @param interviews the list of Interview objects
   * @return a map where the key is the mastery ID and the value is the corresponding Mastery object
   */
  private Map<Long, Mastery> getMasteriesByInterviews(List<Interview> interviews) {
    List<Long> masteryIds = interviews.stream()
        .map(Interview::getMasteryId)
        .toList();
    return masteryService.findByIds(masteryIds).stream()
        .collect(Collectors.toMap(Mastery::getId, mastery -> mastery));
  }

  /**
   * Sends alerts to both the interviewer and candidate about the scheduled interview.
   *
   * @param interviewerRequest the interview request object containing details about the
   *                           interviewer
   * @param candidateRequest   the interview request object containing details about the candidate
   * @param date               the date and time of the scheduled interview
   */
  private void sendInterviewScheduledAlerts(InterviewRequest interviewerRequest,
      InterviewRequest candidateRequest, ZonedDateTime date, List<Interview> interviews) {
    Map<InterviewRequestRole, Long> roleToId = interviews.stream()
        .collect(Collectors.toMap(Interview::getRole, Interview::getId));
    notifyParticipant(candidateRequest, interviewerRequest, date, roleToId.get(CANDIDATE));
    notifyParticipant(interviewerRequest, candidateRequest, date, roleToId.get(INTERVIEWER));
  }

  /**
   * Sends a notification and an email to a participant of an interview about the scheduled
   * interview.
   *
   * @param recipientRequest         the interview request of the recipient of the notification
   * @param secondParticipantRequest the interview request of the other participant in the
   *                                 interview
   * @param interviewStartTimeInUtc  the start time of the interview in UTC
   */
  private void notifyParticipant(InterviewRequest recipientRequest,
      InterviewRequest secondParticipantRequest, ZonedDateTime interviewStartTimeInUtc,
      long interviewId) {

    User recipient = recipientRequest.getUser();
    String recipientEmail = recipient.getEmail();
    String role = String.valueOf(recipientRequest.getRole());

    notificationService.addInterviewScheduled(recipient, role,
        interviewStartTimeInUtc, interviewId);

    emailService.sendInterviewScheduledEmail(recipient, recipientEmail,
        interviewStartTimeInUtc, secondParticipantRequest, interviewId);
  }

  /**
   * Notifies users involved in the interview rejection.
   *
   * @param recipient     The user for whom rejected the interview.
   * @param rejector      The user who rejected the interview.
   */
  private void notifyParticipants(User recipient, User rejector, List<Interview> interviews) {
    Long recipientInterviewId = interviews.stream()
        .filter(i -> i.getUserId() == recipient.getId())
        .map(Interview::getId).findFirst().get();
    Long rejectorInterviewId = interviews.stream()
        .filter(i -> i.getUserId() == rejector.getId())
        .map(Interview::getId).findFirst().get();
    ZonedDateTime scheduledTime = interviews.getFirst().getStartTime();
    notificationService.addRejectInterview(recipient, rejector.getFirstName(),
        scheduledTime, recipientInterviewId);
    notificationService.addRejectInterview(rejector, recipient.getFirstName(),
        scheduledTime, rejectorInterviewId);

    emailService.sendInterviewRejectionMessageEmail(
        recipient, rejector, scheduledTime, recipient.getEmail());
  }

  /**
   * Determines the overall interview status for the currently authenticated user.
   */
  public InterviewsOverallStatusDto getInterviewStatusIndicator(String userTimeZone) {
    long authUserId = userContextProvider.getAuthenticatedUserId();

    List<Interview> interviews = interviewRepository.findAllByUserIdAndIsVisibleTrue(authUserId);

    if (interviews.isEmpty()) {
      return new InterviewsOverallStatusDto(null);
    }

    ZoneId userZoneId = ZoneId.of(userTimeZone);
    ZonedDateTime now = ZonedDateTime.now(userZoneId);

    boolean inProgress = interviews.stream()
        .map(Interview::getStartTime)
        .map(start -> start.withZoneSameInstant(userZoneId))
        .anyMatch(start -> now.isAfter(start) && now.isBefore(start.plusHours(INTERVIEW_DURATION)));
    if (inProgress) {
      return new InterviewsOverallStatusDto(InterviewStatusIndicator.IN_PROGRESS);
    }

    boolean awaitingFeedback = interviews.stream()
        .map(Interview::getStartTime)
        .map(start -> start.withZoneSameInstant(userZoneId))
        .anyMatch(start -> now.isAfter(start.plusHours(INTERVIEW_DURATION)));
    if (awaitingFeedback) {
      return new InterviewsOverallStatusDto(InterviewStatusIndicator.AWAITING_FEEDBACK);
    }

    return new InterviewsOverallStatusDto(null);
  }

  /**
   * Retrieves or creates a meeting room link for the interview pair.
   *
   * @param id ID of the interview for which the meeting link is requested.
   * @return A valid meeting room URL.
   */
  @Transactional
  public String resolveMeetingUrl(long id) {
    Interview interview =
        interviewRepository.findByIdAndUserId(id, userContextProvider.getAuthenticatedUserId())
        .orElseThrow(() -> new InterviewNotFoundException(id));

    if (interview.getRoomUrl() != null) {
      return interview.getRoomUrl();
    }

    String roomUrl = meetingService.createMeeting();

    interviewRepository.updateInterviewsRoomUrlByEventId(interview.getEventId(), roomUrl);

    snsPublisherService.publishMeetingStarting(roomUrl);

    return roomUrl;
  }
}
