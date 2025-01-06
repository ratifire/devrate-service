package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.AchievementDto;
import com.ratifire.devrate.dto.BookmarkDto;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.dto.EducationDto;
import com.ratifire.devrate.dto.EmploymentRecordDto;
import com.ratifire.devrate.dto.EventDto;
import com.ratifire.devrate.dto.InterviewFeedbackDto;
import com.ratifire.devrate.dto.InterviewHistoryDto;
import com.ratifire.devrate.dto.InterviewStatsConductedPassedByDateDto;
import com.ratifire.devrate.dto.LanguageProficiencyDto;
import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.dto.SpecializationDto;
import com.ratifire.devrate.dto.UserDto;
import com.ratifire.devrate.dto.UserMainHardSkillsDto;
import com.ratifire.devrate.dto.UserMainMasterySkillDto;
import com.ratifire.devrate.dto.UserNameSearchDto;
import com.ratifire.devrate.dto.UserPictureDto;
import com.ratifire.devrate.entity.InterviewHistory;
import com.ratifire.devrate.service.UserService;
import com.ratifire.devrate.service.interview.InterviewHistoryService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling requests related to InterviewHistory information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/interview_histories")
public class InterviewHistoryController {

  private final InterviewHistoryService interviewHistoryService;

  /**
   * Retrieves a list of user`s interview summaries information by user ID.
   *
   * @return the list of user's InterviewSummary information as a DTO
   */
  @GetMapping()
  public InterviewHistory getInterviewHistoryies() {
    return interviewHistoryService.findById();
  }

  /**
   * Retrieves a interview history information by ID.
   *
   * @return the Interview history information as a DTO
   */
  @GetMapping("/{id}")
  public InterviewHistory getInterviewHistory() {
    return interviewHistoryService.findById();
  }

  /**
   * Deletes the association between a user and an interview summary.
   *
   * @param id     the ID of the interview summary to be removed from the user's associations
   */
  @DeleteMapping("/{id}")
  public void deleteInterviewSummary(@PathVariable long id) {
    interviewHistoryService.deleted(id);
  }

}
