package com.ratifire.devrate.util.zoom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.network.ZoomApiClient;
import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for the {@link ZoomApiService}.
 */
@ExtendWith(MockitoExtension.class)
public class ZoomApiServiceTest {

  @InjectMocks
  private ZoomApiService zoomApiService;

  @Mock
  private ZoomApiClient zoomApiClient;

  @Mock
  private ObjectMapper objectMapper;

  private ZoomCreateMeetingResponse createMeetingResponse;
  private String topic;
  private String description;
  private LocalDateTime time;

  @BeforeEach
  public void before() {
    createMeetingResponse = new ZoomCreateMeetingResponse();
    createMeetingResponse.setId(89635331342L);
    createMeetingResponse.setJoinUrl("https://us05web.zoom.us/j/89635331342?pwd=DxasGmWCAMBFm3.1");

    topic = "Test Topic";
    description = "Test Description";
    time = LocalDateTime.now();
  }

  @Test
  public void createMeetingTest() {
    when(zoomApiClient.post(any(), any(), any())).thenReturn(
        Optional.ofNullable(createMeetingResponse));

    Optional<ZoomCreateMeetingResponse> response = zoomApiService.createMeeting(topic, description,
        time);

    assertEquals(createMeetingResponse.getJoinUrl(), response.get().getJoinUrl());
    verify(zoomApiClient, times(1)).post(any(), any(), any());
  }

  @Test
  public void deleteMeetingTest() throws ZoomApiException {
    doNothing().when(zoomApiClient).delete(any());
    zoomApiService.deleteMeeting(any());
    verify(zoomApiClient, times(1)).delete(any());
  }

  @Test
  public void deleteMeeting_ThrowsZoomApiExceptionTest() throws ZoomApiException {
    doThrow(ZoomApiException.class).when(zoomApiClient).delete(any());
    assertThrows(ZoomApiException.class, () -> zoomApiService.deleteMeeting(any()));
    verify(zoomApiClient, times(1)).delete(any());
  }
}
