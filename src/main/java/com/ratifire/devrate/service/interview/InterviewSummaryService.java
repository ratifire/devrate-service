package com.ratifire.devrate.service.interview;

import com.ratifire.devrate.entity.InterviewSummary;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.Interview;
import com.ratifire.devrate.enums.InterviewRequestRole;
import com.ratifire.devrate.exception.InterviewSummaryNotFoundException;
import com.ratifire.devrate.exception.RoleNotFoundException;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
import com.ratifire.devrate.repository.UserRepository;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing interview summaries.
 */
@Service
@RequiredArgsConstructor
public class InterviewSummaryService {

  private final InterviewSummaryRepository interviewSummaryRepository;
  private UserRepository userRepository;

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Retrieves an InterviewSummary entity by its identifier.
   *
   * @param id the unique identifier of the InterviewSummary to be retrieved.
   * @return the InterviewSummary associated with the provided id.
   * @throws InterviewSummaryNotFoundException if no InterviewSummary is found for the given id.
   */
  public InterviewSummary findById(long id) {
    return interviewSummaryRepository.findById(id).orElseThrow(() ->
        new InterviewSummaryNotFoundException(id));
  }

  /**
   * Saves an interview summary and updates the interview summaries list for each participant.
   *
   * @param summary      the interview summary to be saved
   * @param participants list of users who participated in the interview
   */
  public void save(InterviewSummary summary, List<User> participants) {
    interviewSummaryRepository.save(summary);

    participants.forEach(user -> user.getInterviewSummaries().add(summary));
    userRepository.saveAll(participants);
  }

  /**
   * Creates an interview summary based on the provided interview and end time.
   *
   * @param interview the Interview object used to retrieve details for creating the summary
   * @param endTime   the end time of the interview, used to calculate the duration
   */
  public void createInterviewSummary(Interview interview, String endTime) {
    User interviewer = interview.getInterviewerRequest().getUser();
    User candidate = interview.getCandidateRequest().getUser();
    ZonedDateTime startTime = interview.getStartTime();

    InterviewSummary interviewSummary = InterviewSummary.builder()
        .date(startTime.toLocalDate())
        .duration(Duration.between(startTime, ZonedDateTime.parse(endTime)).toMinutes())
        .candidateId(candidate.getId())
        .interviewerId(interviewer.getId())
        .build();

    save(interviewSummary, List.of(interviewer, candidate));
  }

  /**
   * Add a comment to the appropriate field in the InterviewSummary.
   *
   * @param id              the identifier of the InterviewSummary to which the comment is to be
   *                        added.
   * @param reviewerId      the ID of the reviewer.
   * @param reviewerComment the text of the comment to be saved.
   * @return the role of the reviewer in the interview
   */
  public InterviewRequestRole addComment(long id, long reviewerId, String reviewerComment) {
    InterviewSummary summary = findById(id);
    InterviewRequestRole role = getParticipantRole(summary, reviewerId);
    switch (role) {
      case CANDIDATE -> summary.setCandidateComment(reviewerComment);
      case INTERVIEWER -> summary.setInterviewerComment(reviewerComment);
      default -> throw new RoleNotFoundException("Unknown role: " + role);
    }
    interviewSummaryRepository.save(summary);
    return role;
  }

  /**
   * Determines the role of a participant in an interview based on their user ID.
   *
   * @param summary    the InterviewSummary containing the interview details
   * @param reviewerId the user ID of the participant whose role is to be determined
   * @return the InterviewRequestRole of the participant
   */
  private InterviewRequestRole getParticipantRole(InterviewSummary summary, long reviewerId) {
    return summary.getCandidateId() == reviewerId
        ? InterviewRequestRole.CANDIDATE
        : InterviewRequestRole.INTERVIEWER;
  }
}