package com.ratifire.devrate.repository.interview;

import com.ratifire.devrate.entity.interview.InterviewRequest;
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
@Repository
@RepositoryRestResource(exported = false)
public interface InterviewRequestRepository extends JpaRepository<InterviewRequest, Long> {
  Optional<InterviewRequest> findByIdAndUser_Id(long id, long userId);

  void deleteByIdAndUser_Id(long id, long userId);

  @Query("SELECT r.mastery.id FROM InterviewRequest r WHERE r.id = :id")
  Optional<Long> findMasteryIdById(@Param("id") Long id);

  @Query("SELECT r FROM InterviewRequest r WHERE r.id IN (:ids)")
  List<InterviewRequest> findByIds(@Param("ids") List<Long> ids);

  List<InterviewRequest> findAllByMastery_IdAndUser_Id(long masteryId, long userId);
}
