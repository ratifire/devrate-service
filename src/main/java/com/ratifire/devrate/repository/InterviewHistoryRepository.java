package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.InterviewHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on interview-history entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewHistoryRepository extends JpaRepository<InterviewHistory, Long> {

  InterviewHistory findByIdAndUserId(Long id, Long userId);

  Page<InterviewHistory> findAllByUserId(long userId, Pageable pageable);

}
