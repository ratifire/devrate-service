package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.interview.InterviewHistory;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
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

  Optional<InterviewHistory> findByIdAndUserIdAndIsVisibleTrue(Long id, Long userId);

  Page<InterviewHistory> findAllByUserIdAndIsVisibleTrue(long userId, Pageable pageable);

  Optional<InterviewHistory> findByInterviewIdAndUserId(long interviewId, long userId);

  List<InterviewHistory> findByUserIdAndDateTimeBetween(long userId, ZonedDateTime from,
      ZonedDateTime to);
}
