package com.ratifire.devrate.service.interview;

import static com.ratifire.devrate.enums.InterviewRequestRole.CANDIDATE;
import static com.ratifire.devrate.enums.InterviewRequestRole.INTERVIEWER;

import com.ratifire.devrate.dto.InterviewDto;
import com.ratifire.devrate.dto.PairedParticipantDto;
import com.ratifire.devrate.entity.Event;
import com.ratifire.devrate.entity.Mastery;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.EventService;
import com.ratifire.devrate.service.MasteryService;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Service for handling Interview operations.
 */
@Service
@RequiredArgsConstructor
public class InterviewService {

  private final ZoomApiService zoomApiService; //if needed
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
   * Retrieves a list of all interviews associated with the currently authenticated user.
   *
   * @return a list of InterviewDto objects representing the user's interviews
   */
  public List<InterviewDto> getAll() {
    long userId = userContextProvider.getAuthenticatedUserId();
    List<Interview> interviews = interviewRepository.findByUserId(userId);

    if (interviews.isEmpty()) {
      return List.of();
    }

    return interviews.stream()
        .map(interview -> {
          Mastery mastery = masteryService.getMasteryById(interview.getMasteryId());
          long hostId = interviewRepository.findUserIdByInterviewIdAndUserIdNot(
              interview.getInterviewId(),
              interview.getUserId()
          ).orElseThrow(() -> new IllegalStateException("Host not found"));

          return mapper.toDto(
              interview,
              mastery.getLevel(),
              mastery.getSpecialization().getName(),
              hostId);
        })
        .toList();
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
    long interviewId = generateInterviewId();
    ZonedDateTime date = matchedUsers.getDate();

    //TODO: createMeeting needs to be reimplemented for using another meeting provider
    String joinUrl = "temporary join url";
    //    ZoomCreateMeetingResponse zoomMeeting =
    //    zoomApiService.createMeeting("Topic", "Agenda", date)
    //        .orElseThrow(() -> new IllegalStateException("Zoom meeting creation failed."));
    //    String joinUrl = zoomMeeting.getJoinUrl();

    Interview interviewer = buildInterview(interviewerId, interviewerRequestId, interviewId,
        INTERVIEWER, joinUrl, date);
    Interview candidate = buildInterview(candidateId, candidateRequestId, interviewId, CANDIDATE,
        joinUrl, date);
    interviewRepository.saveAll(List.of(interviewer, candidate));

    populateInterviewRequestAssignedDate(interviewerRequestId, candidateRequestId,
        interviewer.getStartTime());

    Event event = eventService.buildEvent(interviewId, candidateId, interviewerId, joinUrl, date);
    eventService.save(event, List.of(interviewerId, candidateId));

    List<InterviewRequest> requests = interviewRequestService.findByIds(
        List.of(interviewerRequestId, candidateRequestId));
    Map<Long, InterviewRequest> requestMap = requests.stream()
        .collect(Collectors.toMap(InterviewRequest::getId, request -> request));

    sendInterviewScheduledAlerts(
        requestMap.get(interviewerRequestId), requestMap.get(candidateRequestId), date, joinUrl);
  }

  private void populateInterviewRequestAssignedDate(long interviewerRequestId,
      long candidateRequestId, ZonedDateTime assignedDate) {
    List<InterviewRequest> scheduledInterviewRequests = interviewRequestService.findByIds(
        List.of(interviewerRequestId, candidateRequestId));

    if (scheduledInterviewRequests.isEmpty()) {
      return;
    }

    scheduledInterviewRequests.forEach(request -> {
      if (CollectionUtils.isEmpty(request.getAssignedDates())) {
        request.setAssignedDates(List.of(assignedDate));
      } else {
        request.getAssignedDates().add(assignedDate);
      }
    });

    interviewRequestService.saveAll(scheduledInterviewRequests);
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
    eventService.deleteAllByEventTypeId(interviews.getFirst().getInterviewId());
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
  private Interview buildInterview(long userId, long requestId, long interviewId,
      InterviewRequestRole role, String roomUrl, ZonedDateTime date) {

    long masteryId = interviewRequestService.findMasteryId(requestId)
        .orElseThrow(() -> new IllegalStateException(
            "Mastery ID not found for interview request with id: " + requestId));

    return Interview.builder()
        .userId(userId)
        .masteryId(masteryId)
        .interviewId(interviewId)
        .role(role)
        .roomUrl(roomUrl)
        .startTime(date)
        .build();
  }

  /**
   * Generates a unique interview ID based.
   *
   * @return a unique long value representing the interview ID
   */
  private long generateInterviewId() {
    return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
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