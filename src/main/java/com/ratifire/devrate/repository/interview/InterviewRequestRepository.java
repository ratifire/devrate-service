package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.enums.InterviewRequestRole;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing InterviewRequest entities.
 */
@Deprecated
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
          AND req.user != :#{#request.user}
          AND mast.level <= :#{#request.mastery.level}
          AND EXISTS (SELECT 1 FROM req.availableDates d WHERE d IN :#{#request.availableDates})
          AND (:#{#ignoreList.size()} = 0 OR req.user NOT IN :ignoreList)
      """)
  List<InterviewRequest> findMatchedCandidates(@Param("request") InterviewRequest request,
      List<User> ignoreList);

  @Query("""
          SELECT req FROM InterviewRequest req
          JOIN req.mastery mast
          JOIN mast.specialization spec
          WHERE spec.name = :#{#request.mastery.specialization.name}
          AND req.active = true
          AND req.id != :#{#request.id}
          AND req.role != :#{#request.role}
          AND req.user != :#{#request.user}
          AND mast.level >= :#{#request.mastery.level}
          AND EXISTS (SELECT 1 FROM req.availableDates d WHERE d IN :#{#request.availableDates})
          AND (:#{#ignoreList.size()} = 0 OR req.user NOT IN :ignoreList)
      """)
  List<InterviewRequest> findMatchedInterviewers(@Param("request") InterviewRequest request,
      List<User> ignoreList);

  List<InterviewRequest> findByActiveTrueAndExpiredAtBefore(ZonedDateTime currentDateTime);

  Optional<InterviewRequest> findByUserIdAndRoleAndMastery_Id(long userId,
      InterviewRequestRole role, long masteryId);
}
