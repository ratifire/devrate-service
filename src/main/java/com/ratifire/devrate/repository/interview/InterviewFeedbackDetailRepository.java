package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.interview.InterviewFeedbackDetail;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing InterviewFeedbackDetail entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewFeedbackDetailRepository extends
    JpaRepository<InterviewFeedbackDetail, Long> {

  @Query(value = "SELECT user_id FROM interview_feedback_details WHERE id = :resourceId",
      nativeQuery = true)
  Optional<Long> findUserIdByInterviewFeedbackDetailId(@Param("resourceId") long resourceId);

  @Modifying
  @Query("DELETE FROM InterviewFeedbackDetail i WHERE i.startTime <= :cutoffDate")
  void deleteExpiredFeedbackDetails(@Param("cutoffDate") ZonedDateTime cutoffDate);
}