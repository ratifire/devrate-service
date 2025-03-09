package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.interview.InterviewRequest;
import com.ratifire.devrate.entity.interview.InterviewRequestTimeSlot;
import com.ratifire.devrate.enums.TimeSlotStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing InterviewRequestTimeSlot entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewRequestTimeSlotRepository extends
    JpaRepository<InterviewRequestTimeSlot, Long> {

  @Modifying
  @Query("UPDATE InterviewRequestTimeSlot t SET t.status = :updatedStatus "
      + "WHERE t.interviewRequest IN :requests AND DATE(t.dateTime) = DATE(:scheduledDate)")
  void updateTimeSlotStatus(@Param("requests") List<InterviewRequest> scheduledInterviewRequests,
      @Param("scheduledDate") ZonedDateTime scheduledDate,
      @Param("updatedStatus") TimeSlotStatus updatedStatus);

  void deleteByInterviewRequestAndDateTimeNotIn(InterviewRequest request,
      Set<ZonedDateTime> updatedDateTime);
}