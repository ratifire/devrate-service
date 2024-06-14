package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.InterviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on interview-summary entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewSummaryRepository extends JpaRepository<InterviewSummary, Long> {

}
