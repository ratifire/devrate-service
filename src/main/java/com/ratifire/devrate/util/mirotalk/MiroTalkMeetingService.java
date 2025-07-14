package com.ratifire.devrate.util.mirotalk;

import com.ratifire.devrate.service.MeetingService;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service for interacting with the MiroTalk API to create meetings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "meeting.service.type", havingValue = "MiroTalk")
public class MiroTalkMeetingService implements MeetingService {

  @Value("${mirotalk.api.url}")
  private String miroTalkApiUrl;
  @Value("${mirotalk.api.key}")
  private String miroTalkApiKey;

  private static final String AUTHORIZATION_HEADER = "authorization";

  private final RestTemplateBuilder restTemplate;

  /**
   * Creates a new meeting in MiroTalk by sending a POST request to the MiroTalk API.
   *
   * @return the URL of the created meeting if successful, or an empty string if failed.
   */
  @Override
  public String createMeeting() {
    String url = miroTalkApiUrl + "/api/v1/meeting";

    HttpHeaders headers = new HttpHeaders();
    headers.set(AUTHORIZATION_HEADER, miroTalkApiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<MiroTalkMeetingResponse> response = restTemplate.build()
          .exchange(url, HttpMethod.POST, entity, MiroTalkMeetingResponse.class);

      log.info("MiroTalk meeting creation response: {}", response.getBody());

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return response.getBody().getMeeting();
      }

      throw new MeetingServiceException("Failed to create meeting. Status code: "
          + response.getStatusCode());
    } catch (Exception e) {
      throw new MeetingServiceException("Error creating meeting with MiroTalk API", e);
    }
  }

  // TODO: remove with Zoom
  public String createMeeting(String topic, String meetDescription, ZonedDateTime startTime) {
    return "";
  }
}