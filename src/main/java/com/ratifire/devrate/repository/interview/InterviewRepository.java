package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.interview.Interview;
import java.util.List;
import java.util.Optional;
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

  List<Interview> findByUserId(long userId);

  List<Interview> findByEventId(long eventId);

  @Query("SELECT i FROM Interview i WHERE i.eventId = "
      + "(SELECT eventId FROM Interview WHERE id = :id)")
  List<Interview> findInterviewPairById(@Param("id") long id);

  @Query("SELECT i.userId FROM Interview i WHERE i.eventId ="
      + " :eventId AND i.userId <> :userId")
  Optional<Long> findUserIdByInterviewIdAndUserIdNot(@Param("eventId") long eventId,
      @Param("userId") long userId);

  //TODO: Method needs to be reimplemented

  //  @Query("SELECT i.id "
  //      + "FROM Interview i "
  //      + "WHERE i.candidateRequest.mastery.specialization.id = :specializationId "
  //      + "   OR i.interviewerRequest.mastery.specialization.id = :specializationId")
  //  Long findFirstBySpecializationId(@Param("specializationId") Long specializationId);
}
