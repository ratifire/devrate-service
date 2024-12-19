package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.dto.UpdateParticipantRequestDto;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending HTTP requests to the matcher-service.
 */
@Service
@RequiredArgsConstructor
public class MatcherServiceHttpClient {

  @Value("${matching-service.url}")
  private String matchingServiceUrl;
  private final RestTemplateBuilder restTemplate;

  /**
   * Sends participant data to the matcher-service via HTTP.
   *
   * @param request the interview request to process and send.
   */
  // TODO: 6764178ae4f4c13c037caf6d used for testing should be changed to the participantId from
  //  the matcher-service
  public void update(InterviewRequest request) {
    String endpoint = matchingServiceUrl + "/participants/6764178ae4f4c13c037caf6d";

    UpdateParticipantRequestDto participant = UpdateParticipantRequestDto.builder()
        .desiredInterview(request.getDesiredInterview())
        .matchedInterview(0)
        .dates(convertToDates(request.getAvailableDates()))
        .build();
    restTemplate.build().put(endpoint, participant, Void.class);
  }

  private Set<Date> convertToDates(List<ZonedDateTime> availableDates) {
    return availableDates.stream()
        .map(zdt -> Date.from(zdt.toInstant()))
        .collect(Collectors.toSet());
  }
}
