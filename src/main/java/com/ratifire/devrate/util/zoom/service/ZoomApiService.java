package com.ratifire.devrate.util.zoom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.service.MeetingService;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.network.ZoomApiClient;
import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingRequest;
import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingRequest.Settings;
import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingResponse;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with the Zoom API.
 */
@Service
@RequiredArgsConstructor
public class ZoomApiService implements MeetingService {

  @Value("${zoom.oauth2.api.url}")
  private String zoomApiUrl;

  private static final String ZOOM_USER_BASE_URL = "/users";

  private final ZoomApiClient zoomApiClient;
  private final ObjectMapper objectMapper;
  private static final Logger logger = LoggerFactory.getLogger(ZoomApiService.class);

  /**
   * Creates a Zoom meeting.
   *
   * @param topic           the topic or title of the meeting.
   * @param meetDescription the description or agenda of the meeting.
   * @param startTime       the start time of the meeting.
   * @return the join URL for the created meeting.
   */
  public String createMeeting(String topic, String meetDescription,
      ZonedDateTime startTime) {
    String jsonRequest = buildJsonCreateMeetingRequest(topic, meetDescription, startTime);
    String url = zoomApiUrl + ZOOM_USER_BASE_URL + "/me/meetings";

    return zoomApiClient.post(url, jsonRequest, ZoomCreateMeetingResponse.class)
        .map(ZoomCreateMeetingResponse::getJoinUrl)
        .orElseThrow(() -> new ZoomApiException("Cannot create meeting by URL" + url));
  }

  private String buildJsonCreateMeetingRequest(String topic, String meetDescription,
      ZonedDateTime startTime) {
    try {
      ZoomCreateMeetingRequest request = ZoomCreateMeetingRequest.builder()
          .topic(topic)
          .type(2)
          .agenda(meetDescription)
          .duration(40)
          .startTime(startTime)
          .timezone("UTC")
          .settings(Settings.builder()
              .joinBeforeHost(true)
              .meetingAuthentication(false)
              .jbhTime(5)
              .privateMeeting(true)
              .build())
          .build();
      return objectMapper.writeValueAsString(request);
    } catch (JsonProcessingException e) {
      logger.error("Error processing JSON request", e);
      return StringUtils.EMPTY;
    }
  }

  /**
   * Deletes a Zoom meeting.
   *
   * @param zoomMeetingId the ID of the meeting to be deleted.
   * @throws ZoomApiException if an error occurs while deleting the meeting.
   */
  public void deleteMeeting(Long zoomMeetingId) throws ZoomApiException {
    String url = String.format("%s/meetings/%s", zoomApiUrl, zoomMeetingId);
    zoomApiClient.delete(url);
  }
}
