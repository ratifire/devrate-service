package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Orchestrator responsible for coordinating interactions with the matcher-service via HTTP and
 * queues.
 */
@Service
@RequiredArgsConstructor
public class MatchingServiceOrchestrator {

  private final MatcherServiceQueueSender queueSender;
  private final MatchingServiceHttpClient httpClient;

  /**
   * Sends an interview request to the matcher-service queue.
   *
   * @param request the interview request to process and send.
   */
  public void sendToQueue(InterviewRequest request) {
    queueSender.sendToQueue(request);
  }

  /**
   * Updates interview request data in the matcher-service via HTTP.
   *
   * @param request the interview request to process and send.
   */
  public void update(InterviewRequest request) {
    httpClient.update(request);
  }
}
