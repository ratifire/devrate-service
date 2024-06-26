package com.ratifire.devrate.util.zoom;

import com.ratifire.devrate.util.zoom.payloads.ZoomCreateMeetingResponse;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller will be deleted. Only for testing Zoom functionality.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/zoom")
public class ZoomController {

  private ZoomApiService zoomApiService;

  @PostMapping("/create-meeting")
  public Optional<ZoomCreateMeetingResponse> createMeeting(@RequestParam String topic,
      @RequestParam String description) {
    return zoomApiService.createMeeting(topic, description, LocalDateTime.now());
  }
}