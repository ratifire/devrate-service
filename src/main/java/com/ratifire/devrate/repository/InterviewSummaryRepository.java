package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.InterviewSummary;
import java.time.LocalDate;
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
  @Query("SELECT i FROM InterviewSummary i "
      + "WHERE (i.candidateId = :id OR i.interviewerId = :id) "
      + "AND i.date BETWEEN :from AND :to")
  List<InterviewSummary> findByCandidateOrInterviewerAndDateBetween(
      @Param("id") long id,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);
}