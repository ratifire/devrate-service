package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.repository.interview.InterviewHistoryRepository;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import com.ratifire.devrate.service.NotificationService;
import com.ratifire.devrate.service.UserService;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service responsible for processing scheduled interview tasks.
 */
@Service
@AllArgsConstructor
@Slf4j
public class InterviewSchedulerService {

  private final InterviewRepository interviewRepository;
  private final NotificationService notificationService;
  private final InterviewHistoryRepository interviewHistoryRepository;
  private final UserService userService;

  /**
   * Scheduled method that processes interviews from the last 1 hour and checks
   * whether they exist in the interview history.
   */
  @Scheduled(cron = "0 0 * * * *")
  public void processInterviewsForLastHour() {
    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);
    ZonedDateTime oneHourAgo = now.minusHours(1);

    List<Interview> interviews =
        interviewRepository.findByStartTimeGreaterThanEqualAndStartTimeLessThan(oneHourAgo, now);
    if (interviews.isEmpty()) {
      log.info("No needed feedback interviews found in the last hour ({} - {}).", oneHourAgo, now);
      return;
    }

    List<Long> interviewIds = interviews.stream()
        .map(Interview::getId)
        .toList();

    List<Long> existingInterviewIds = interviewHistoryRepository.findExistingInterviewIds(
        interviewIds);

    List<Interview> feedbackNeededInterviews = interviews.stream()
        .filter(i -> !existingInterviewIds.contains(i.getId()))
        .toList();

    feedbackNeededInterviews.forEach(interview -> {
      User user = userService.findById(interview.getUserId());
      notificationService.addInterviewFeedbackDetail(user, interview.getId(), user.getEmail());
    });
  }
}
