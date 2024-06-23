package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing InterviewRequest entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewRequestRepository extends JpaRepository<InterviewRequest, Long> {

  @Query("""
          SELECT req FROM InterviewRequest req
          JOIN req.mastery mast
          JOIN mast.specialization spec
          WHERE spec.name = :#{#request.mastery.specialization.name}
          AND req.active = true
          AND req.id != :#{#request.id}
          AND req.role != :#{#request.role}
          AND mast.level <= :#{#request.mastery.level}
          AND EXISTS (SELECT 1 FROM req.availableDates d WHERE d IN :#{#request.availableDates})
      """)
  List<InterviewRequest> findMatchedCandidates(@Param("request") InterviewRequest request);

  @Query("""
          SELECT req FROM InterviewRequest req
          JOIN req.mastery mast
          JOIN mast.specialization spec
          WHERE spec.name = :#{#request.mastery.specialization.name}
          AND req.active = true
          AND req.id != :#{#request.id}
          AND req.role != :#{#request.role}
          AND mast.level >= :#{#request.mastery.level}
          AND EXISTS (SELECT 1 FROM req.availableDates d WHERE d IN :#{#request.availableDates})
      """)
  List<InterviewRequest> findMatchedInterviewers(@Param("request") InterviewRequest request);

  List<InterviewRequest> findByActiveTrueAndExpiredAtBefore(ZonedDateTime currentDateTime);
}
