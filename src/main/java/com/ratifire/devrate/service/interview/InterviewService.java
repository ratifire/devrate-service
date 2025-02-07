package com.ratifire.devrate.service.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.CANDIDATE;
import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.dto.InterviewDto;
import com.ratifire.devrate.dto.InterviewEventDto;
import com.ratifire.devrate.dto.PairedParticipantDto;
import com.ratifire.devrate.dto.ParticipantDto;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.enums.MasteryLevel;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.EventService;
import com.ratifire.devrate.service.MasteryService;
import com.ratifire.devrate.service.MeetingService;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling Interview operations.
 */
@Service
@RequiredArgsConstructor
public class InterviewService {

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

  /**
   * Retrieves a all interviews associated with the currently authenticated user.
   *
   * @return an InterviewDto objects representing the user's interviews
   */
  public Page<InterviewDto> findAll(int page, int size) {
    long userId = userContextProvider.getAuthenticatedUserId();
    Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
    Page<Interview> interviews = interviewRepository.findByUserId(userId, pageable);

    return interviews.map(interview -> {
      Mastery mastery = masteryService.getMasteryById(interview.getMasteryId());
      long hostId = interviewRepository.findUserIdByInterviewIdAndUserIdNot(
          interview.getEventId(),
          interview.getUserId()
      ).orElseThrow(() -> new IllegalStateException("Host not found"));
      User host = userService.findById(hostId);

      return mapper.toDto(
          interview,
          mastery.getLevel(),
          mastery.getSpecialization().getName(),
          hostId,
          host.getFirstName(),
          host.getLastName());
    });
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

    String joinUrl = meetingService.createMeeting("Topic", "Agenda", date);

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
    Interview interviewer = buildInterview(interviewerId, interviewerRequestId, eventId,
        INTERVIEWER, joinUrl, date, interviewerRequestComment);
    String candidateRequestComment = extractCommentForRole(requests, CANDIDATE);
    Interview candidate = buildInterview(candidateId, candidateRequestId, eventId, CANDIDATE,
        joinUrl, date, candidateRequestComment);
    interviewRepository.saveAll(List.of(interviewer, candidate));

    interviewRequestService.updateAssignedDates(interviewerRequestId, candidateRequestId,
        interviewer.getStartTime());

    Map<Long, InterviewRequest> requestMap = requests.stream()
        .collect(Collectors.toMap(InterviewRequest::getId, request -> request));

    sendInterviewScheduledAlerts(
        requestMap.get(interviewerRequestId), requestMap.get(candidateRequestId), date, joinUrl);
  }

  private String extractCommentForRole(List<InterviewRequest> requests, InterviewRequestRole role) {
    return requests.stream()
        .filter(r -> r.getRole() == role)
        .map(InterviewRequest::getComment)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("");
  }

  /**
   * Deletes a rejected interview by its ID and returns the deleted interview.
   *
   * @param id the ID of the interview to be deleted
   */
  @Transactional
  public void deleteRejected(long id) {
    List<Interview> interviews = interviewRepository.findInterviewPairById(id);
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
        interviews.getFirst().getStartTime());
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
   * Builds an Interview object using the provided parameters.
   *
   * @return the built Interview object
   */
  private Interview buildInterview(long userId, long requestId, long eventId,
      InterviewRequestRole role, String roomUrl, ZonedDateTime date, String requestComment) {

    long masteryId = interviewRequestService.findMasteryId(requestId)
        .orElseThrow(() -> new IllegalStateException(
            "Mastery ID not found for interview request with id: " + requestId));

    return Interview.builder()
        .userId(userId)
        .masteryId(masteryId)
        .eventId(eventId)
        .role(role)
        .roomUrl(roomUrl)
        .startTime(date)
        .requestComment(requestComment)
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
   * @param roomUrl            the URL of the interview room
   */
  private void sendInterviewScheduledAlerts(InterviewRequest interviewerRequest,
      InterviewRequest candidateRequest, ZonedDateTime date, String roomUrl) {
    notifyParticipant(candidateRequest, interviewerRequest, date, roomUrl);
    notifyParticipant(interviewerRequest, candidateRequest, date, roomUrl);
  }

  /**
   * Sends a notification and an email to a participant of an interview about the scheduled
   * interview.
   *
   * @param recipientRequest         the interview request of the recipient of the notification
   * @param secondParticipantRequest the interview request of the other participant in the
   *                                 interview
   * @param interviewStartTimeInUtc  the start time of the interview in UTC
   * @param zoomJoinUrl              the join url to the zoom meeting
   */
  private void notifyParticipant(InterviewRequest recipientRequest,
      InterviewRequest secondParticipantRequest, ZonedDateTime interviewStartTimeInUtc,
      String zoomJoinUrl) {

    User recipient = recipientRequest.getUser();
    String recipientEmail = recipient.getEmail();
    String role = String.valueOf(recipientRequest.getRole());

    notificationService.addInterviewScheduled(recipient, role,
        interviewStartTimeInUtc, recipientEmail);

    emailService.sendInterviewScheduledEmail(recipient, recipientEmail,
        interviewStartTimeInUtc, secondParticipantRequest, zoomJoinUrl);
  }

  /**
   * Notifies users involved in the interview rejection.
   *
   * @param recipient     The user for whom rejected the interview.
   * @param rejector      The user who rejected the interview.
   * @param scheduledTime The scheduled time of the interview.
   */
  private void notifyParticipants(User recipient, User rejector, ZonedDateTime scheduledTime) {
    String recipientEmail = recipient.getEmail();
    notificationService.addRejectInterview(recipient, rejector.getFirstName(),
        scheduledTime, recipientEmail);
    String rejectorEmail = rejector.getEmail();
    notificationService.addRejectInterview(rejector, recipient.getFirstName(),
        scheduledTime, rejectorEmail);

    emailService.sendInterviewRejectionMessageEmail(
        recipient, rejector, scheduledTime, recipientEmail);
  }
}