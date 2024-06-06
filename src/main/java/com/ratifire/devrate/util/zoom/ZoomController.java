package com.ratifire.devrate.util.zoom;

import com.ratifire.devrate.util.zoom.exception.ZoomApiException;
import com.ratifire.devrate.util.zoom.service.ZoomApiService;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ZoomController.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/zoom")
public class ZoomController {

  private ZoomApiService zoomApiService;

  @PostMapping("/create-meeting")
  public String createMeeting() throws ZoomApiException {
    return zoomApiService.createMeeting("topic", "desc", LocalDateTime.now());
  }
}
