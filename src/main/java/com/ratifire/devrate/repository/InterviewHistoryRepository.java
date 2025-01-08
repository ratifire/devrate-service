package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.InterviewHistory;
import java.util.List;
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

  Page<InterviewHistory> findAllByUserId(long userId, Pageable pageable);

}
