package com.ratifire.devrate.service.authorization;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.repository.AchievementRepository;
import com.ratifire.devrate.repository.BookmarkRepository;
import com.ratifire.devrate.repository.EducationRepository;
import com.ratifire.devrate.repository.EmploymentRecordRepository;
import com.ratifire.devrate.repository.InterviewSummaryRepository;
import com.ratifire.devrate.repository.MasteryRepository;
import com.ratifire.devrate.repository.NotificationRepository;
import com.ratifire.devrate.repository.SkillRepository;
import com.ratifire.devrate.repository.SpecializationRepository;
import com.ratifire.devrate.repository.interview.InterviewFeedbackDetailRepository;
import com.ratifire.devrate.repository.interview.InterviewRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for authorizing resource access based on user ownership.
 */
@Service
@RequiredArgsConstructor
public class ResourceAuthorizationService {

  private final NotificationRepository notificationRepository;
  private final AchievementRepository achievementRepository;
  private final EducationRepository educationRepository;
  private final EmploymentRecordRepository employmentRecordRepository;
  private final BookmarkRepository bookmarkRepository;
  private final SpecializationRepository specializationRepository;
  private final MasteryRepository masteryRepository;
  private final SkillRepository skillRepository;
  private final InterviewSummaryRepository interviewSummaryRepository;
  private final InterviewRepository interviewRepository;
  private final InterviewFeedbackDetailRepository interviewFeedbackDetailRepository;
  private final Map<String, ResourceOwnerVerifier> resourceTypeToOwnerVerifier;


  /**
   * Initializes the resource owner verifiers map with the appropriate verification methods.
   */
  @PostConstruct
  public void initResourceOwnerVerifiers() {
    resourceTypeToOwnerVerifier.put("notifications", this::isNotificationOwnedByUser);
    resourceTypeToOwnerVerifier.put("achievements", this::isAchievementOwnedByUser);
    resourceTypeToOwnerVerifier.put("educations", this::isEducationOwnedByUser);
    resourceTypeToOwnerVerifier.put("employment-records", this::isEmploymentRecordOwnedByUser);
    resourceTypeToOwnerVerifier.put("bookmarks", this::isBookmarkOwnedByUser);
    resourceTypeToOwnerVerifier.put("specializations", this::isSpecializationOwnedByUser);
    resourceTypeToOwnerVerifier.put("masteries", this::isMasteryOwnedByUser);
    resourceTypeToOwnerVerifier.put("skills", this::isSkillOwnedByUser);
    resourceTypeToOwnerVerifier.put("interview-summaries", this::isInterviewSummaryOwnedByUser);
    resourceTypeToOwnerVerifier.put("interviews", this::isInterviewOwnedByUser);
    resourceTypeToOwnerVerifier.put("interviewFeedbackDetails",
        this::isInterviewFeedbackDetailOwnedByUser);
  }

  /**
   * Checks if the user ID in the path matches the logged-in user ID.
   *
   * @param pathUserId the user ID from the request path
   * @return true if the path user ID matches the logged-in user ID, false otherwise
   */
  public boolean isPathUserIdMatchingLoggedUser(long pathUserId) {
    return pathUserId == getIdLoggedUserFromAuthentication();
  }

  /**
   * Checks if the resource is owned by the logged-in user.
   *
   * @param resourceType the type of the resource
   * @param resourceId   the ID of the resource
   * @return true if the resource is owned by the logged-in user, false otherwise
   */
  public boolean isResourceOwnedByLoggedUser(String resourceType, long resourceId) {
    long loggedUserId = getIdLoggedUserFromAuthentication();
    ResourceOwnerVerifier resourceOwnerVerifier = resourceTypeToOwnerVerifier.get(resourceType);
    return resourceOwnerVerifier.verifyOwner(resourceId, loggedUserId);
  }

  private boolean isNotificationOwnedByUser(long notificationId, long loggedUserId) {
    Optional<Long> ownerUserId = notificationRepository.findUserIdByNotificationId(notificationId);
    return ownerUserId.isPresent() && ownerUserId.get().equals(loggedUserId);
  }

  private boolean isEducationOwnedByUser(long educationId, long loggedUserId) {
    Optional<Long> ownerUserId = educationRepository.findUserIdByEducationId(educationId);
    return ownerUserId.isPresent() && ownerUserId.get().equals(loggedUserId);
  }

  private boolean isAchievementOwnedByUser(long achievementId, long loggedUserId) {
    Optional<Long> ownerUserId = achievementRepository.findUserIdByAchievementId(achievementId);
    return ownerUserId.isPresent() && ownerUserId.get().equals(loggedUserId);
  }

  private boolean isBookmarkOwnedByUser(long bookmarkId, long loggedUserId) {
    Optional<Long> ownerUserId = bookmarkRepository.findUserIdByBookmarkId(bookmarkId);
    return ownerUserId.isPresent() && ownerUserId.get().equals(loggedUserId);
  }

  private boolean isEmploymentRecordOwnedByUser(long employmentRecordId, long loggedUserId) {
    Optional<Long> ownerUserId = employmentRecordRepository.findUserIdByEmploymentRecordId(
        employmentRecordId);
    return ownerUserId.isPresent() && ownerUserId.get().equals(loggedUserId);
  }

  private boolean isSpecializationOwnedByUser(long specializationId, long loggedUserId) {
    Optional<Long> ownerUserId = specializationRepository.findUserIdBySpecializationId(
        specializationId);
    return ownerUserId.isPresent() && ownerUserId.get().equals(loggedUserId);
  }

  private boolean isMasteryOwnedByUser(long masteryId, long loggedUserId) {
    return masteryRepository.findById(masteryId)
        .map(mastery -> mastery.getSpecialization().getUser().getId() == loggedUserId)
        .orElse(false);
  }

  private boolean isSkillOwnedByUser(long skillId, long loggedUserId) {
    return skillRepository.findMasteryIdBySkillId(skillId)
        .flatMap(masteryRepository::findById)
        .map(mastery -> mastery.getSpecialization().getUser().getId() == loggedUserId)
        .orElse(false);
  }

  private boolean isInterviewSummaryOwnedByUser(long interviewSummaryId, long loggedUserId) {
    List<Long> ownerUserIds = interviewSummaryRepository.findUserIdsByInterviewSummaryId(
        interviewSummaryId);
    return ownerUserIds.contains(loggedUserId);
  }

  private boolean isInterviewOwnedByUser(long interviewId, long loggedUserId) {
    return interviewRepository.findById(interviewId)
        .map(interview -> {
          long interviewerId = interview.getInterviewerRequest().getUser().getId();
          long candidateId = interview.getCandidateRequest().getUser().getId();
          return interviewerId == loggedUserId || candidateId == loggedUserId;
        })
        .orElse(false);
  }

  private boolean isInterviewFeedbackDetailOwnedByUser(long interviewFeedbackDetailId,
      long loggedUserId) {
    Optional<Long> ownerUserId =
        interviewFeedbackDetailRepository.findHostFeedbackIdByInterviewFeedbackDetailId(
            interviewFeedbackDetailId);
    return ownerUserId.isPresent() && ownerUserId.get().equals(loggedUserId);
  }

  /**
   * Retrieves the ID of the logged-in user from the security context.
   *
   * @return the ID of the logged-in user
   */
  private long getIdLoggedUserFromAuthentication() {
    UserSecurity userSecurity = (UserSecurity) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    return userSecurity.getUser().getId();
  }
}
