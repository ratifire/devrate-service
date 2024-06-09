package com.ratifire.devrate.util.zoom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.network.ZoomApiClient;
import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingRequest;
import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingRequest.Settings;
import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the Zoom API.
 */
@Service
@RequiredArgsConstructor
public class ZoomApiService {

  @Value("${zoom.oauth2.api.url}")
  private String zoomApiUrl;

  private static final String ZOOM_USER_BASE_URL = "/users";

  private final ZoomApiClient zoomApiClient;
  private final ObjectMapper objectMapper;

  /**
   * Creates a Zoom meeting.
   *
   * @param topic the topic or title of the meeting.
   * @param meetDescription the description or agenda of the meeting.
   * @param startTime the start time of the meeting.
   * @return the join URL for the created meeting.
   * @throws ZoomApiException if an error occurs while creating the meeting.
   */
  public String createMeeting(String topic, String meetDescription, LocalDateTime startTime)
      throws ZoomApiException {
    String jsonRequest = buildJsonCreateMeetingRequest(topic, meetDescription, startTime);
    String url = zoomApiUrl + ZOOM_USER_BASE_URL + "/me/meetings";

    ZoomCreateMeetingResponse response =
        zoomApiClient.post(url, jsonRequest, ZoomCreateMeetingResponse.class);

    return response.getJoinUrl();
  }

  private String buildJsonCreateMeetingRequest(String topic, String meetDescription,
      LocalDateTime startTime) throws ZoomApiException {
    try {
      ZoomCreateMeetingRequest request = ZoomCreateMeetingRequest.builder()
          .topic(topic)
          .type(2)
          .agenda(meetDescription)
          .duration(40)
          .startTime(startTime.toString())
          .timezone("UTC")
          .settings(Settings.builder()
              .joinBeforeHost(true)
              .meetingAuthentication(false)
              .jbhTime(0)
              .privateMeeting(true)
              .build())
          .build();
      return objectMapper.writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new ZoomApiException("Error processing JSON request", e);
    }
  }
}
