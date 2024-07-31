package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.InterviewSummary;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on interview-summary entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewSummaryRepository extends JpaRepository<InterviewSummary, Long> {
  List<InterviewSummary> findByCandidateIdAndDateBetween(
      long candidateId, LocalDate from, LocalDate to);

  List<InterviewSummary> findByInterviewerIdAndDateBetween(
      long interviewerId, LocalDate from, LocalDate to);
}