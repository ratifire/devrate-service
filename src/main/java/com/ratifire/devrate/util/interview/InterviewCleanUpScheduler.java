package com.ratifire.devrate.util.interview;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.email.EmailService;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for cleaning up expired interview requests.
 */
@Service
@RequiredArgsConstructor
public class InterviewCleanUpScheduler {

  private static final long CLEANUP_INTERVAL = 21600000L; // 6 hours in milliseconds

  private final InterviewRequestRepository interviewRequestRepository;
  private final UserSecurityService userSecurityService;
  private final NotificationService notificationService;
  private final EmailService emailService;

  /**
   * Periodically cleans up expired interview requests. This method is scheduled to run at a fixed
   * interval to find and delete interview requests that have expired and are still active.
   */
  @Scheduled(fixedRate = CLEANUP_INTERVAL, initialDelay = 86400000)  //initialDelay parameter is
  // only for development phase, before release it must be removed
  @Transactional
  public void deleteExpiredAndActiveInterviewRequestsTask() {
    ZonedDateTime currentDateTime = ZonedDateTime.now();
    List<InterviewRequest> expiredRequests =
        interviewRequestRepository.findByActiveTrueAndExpiredAtBefore(currentDateTime);

    for (InterviewRequest interviewRequest : expiredRequests) {
      User user = interviewRequest.getUser();
      String email = userSecurityService.findEmailByUserId(user.getId());
      sendInterviewRequestExpiryAlerts(user, email);
    }

    interviewRequestRepository.deleteAll(expiredRequests);
  }

  /**
   * Sends interview request expiry alerts to a user via email and adds a notification.
   *
   * @param user  The user to whom the expiry alerts are sent.
   * @param email The email address of the user.
   */
  private void sendInterviewRequestExpiryAlerts(User user, String email) {
    notificationService.addInterviewRequestExpiryNotification(user, email);
    emailService.sendInterviewRequestExpiryEmail(user, email);
  }
}
