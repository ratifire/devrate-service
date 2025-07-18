package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.dto.projection.InterviewUserMasteryProjection;
import com.ratifire.devrate.entity.interview.Interview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  Optional<Interview> findByIdAndUserId(long id, long userId);

  Page<Interview> findByUserIdAndIsVisibleTrue(long userId, Pageable pageable);

  List<Interview> findByEventId(long eventId);

  @Query("SELECT i FROM Interview i WHERE i.eventId = "
      + "(SELECT eventId FROM Interview WHERE id = :id)")
  List<Interview> findInterviewPairById(@Param("id") long id);

  InterviewUserMasteryProjection findUserIdAndMasterIdByEventIdAndUserIdNot(
      Long eventId, Long userId);

  @Query("SELECT i FROM Interview i WHERE i.eventId = "
      + "(SELECT sub.eventId FROM Interview sub WHERE sub.id = :interviewId) "
      + "AND i.id != :interviewId")
  Optional<Interview> findOppositeInterview(@Param("interviewId") long interviewId);

  List<Interview> findByMasteryIdAndUserId(long masteryId, long userId);

  List<Interview> findByEventIdIn(List<Long> eventIds);

  Optional<Interview> findByIdAndUserIdAndIsVisibleTrue(long id, long userId);

  List<Interview> findAllByUserIdAndIsVisibleTrue(long userId);

  @Modifying
  @Query("UPDATE Interview i SET i.roomUrl = :roomUrl WHERE i.eventId = :eventId")
  void updateInterviewsRoomUrlByEventId(@Param("eventId") long eventId,
      @Param("roomUrl") String roomUrl);
}
