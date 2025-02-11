package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.dto.projection.InterviewUserMasteryProjection;
import com.ratifire.devrate.entity.interview.Interview;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Interview entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewRepository extends JpaRepository<Interview, Long> {

  Page<Interview> findByUserId(long userId, Pageable pageable);

  List<Interview> findByEventId(long eventId);

  @Query("SELECT i FROM Interview i WHERE i.eventId = "
      + "(SELECT eventId FROM Interview WHERE id = :id)")
  List<Interview> findInterviewPairById(@Param("id") long id);

  InterviewUserMasteryProjection findUserIdAndMasterIdByEventIdAndUserIdNot(
      Long eventId, Long userId);

  //TODO: Method needs to be reimplemented

  //  @Query("SELECT i.id "
  //      + "FROM Interview i "
  //      + "WHERE i.candidateRequest.mastery.specialization.id = :specializationId "
  //      + "   OR i.interviewerRequest.mastery.specialization.id = :specializationId")
  //  Long findFirstBySpecializationId(@Param("specializationId") Long specializationId);
}
