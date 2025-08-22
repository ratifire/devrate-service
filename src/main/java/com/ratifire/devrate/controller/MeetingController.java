package com.ratifire.devrate.controller;

import com.ratifire.devrate.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for operations with meeting.
 */
@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingController {

  public final MeetingService meetingService;

  /**
   * Creates a new meeting and returns a link for it.
   */
  @PostMapping
  public String create() {
    return meetingService.createMeeting();
  }
}
