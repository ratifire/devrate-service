package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing InterviewRequest entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewRequestRepository extends JpaRepository<InterviewRequest, Long> {
  Optional<InterviewRequest> findByIdAndUser_Id(long id, long userId);

  void deleteByIdAndUser_Id(long id, long userId);
}
