package com.ratifire.devrate.scheduler;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.repository.interview.InterviewRequestRepository;
import com.ratifire.devrate.service.EmailService;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserService;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
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

  private final InterviewRequestRepository interviewRequestRepository;
  private final UserService userService;
  private final NotificationService notificationService;
  private final EmailService emailService;

  /**
   * Periodically cleans up expired interview requests. This method is scheduled to run at a fixed
   * interval to find and delete interview requests that have expired and are still active.
   */
  @Scheduled(fixedRate = 6, timeUnit = TimeUnit.HOURS, initialDelay = 24)
  @Transactional
  public void deleteExpiredAndActiveInterviewRequestsTask() {
    final var now = ZonedDateTime.now();
    final var expiredRequests = interviewRequestRepository.findByActiveTrueAndExpiredAtBefore(now);

    expiredRequests.stream()
        .map(InterviewRequest::getUser)
        .forEach(this::sendInterviewRequestExpiryAlerts);

    interviewRequestRepository.deleteAll(expiredRequests);
  }

  /**
   * Sends interview request expiry alerts to a user via email and adds a notification.
   *
   * @param user The user to whom the expiry alerts are sent.
   */
  private void sendInterviewRequestExpiryAlerts(User user) {
    String email = userService.findEmailByUserId(user.getId());
    notificationService.addInterviewRequestExpiry(user, email);
    emailService.sendInterviewRequestExpiryEmail(user, email);
  }
}
