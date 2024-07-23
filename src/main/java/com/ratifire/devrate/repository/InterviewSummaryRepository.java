package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.InterviewSummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on interview-summary entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewSummaryRepository extends JpaRepository<InterviewSummary, Long> {

  @Query(value = "SELECT user_id FROM interview_summaries_users WHERE "
      + "interview_summary_id = :resourceId", nativeQuery = true)
  List<Long> findUserIdsByInterviewSummaryId(@Param("resourceId") long resourceId);
}
