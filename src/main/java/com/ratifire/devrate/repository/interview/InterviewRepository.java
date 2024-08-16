package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.interview.Interview;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Interview entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewRepository extends JpaRepository<Interview, Long> {

  Optional<Interview> findByZoomMeetingId(long meetingId);
}
