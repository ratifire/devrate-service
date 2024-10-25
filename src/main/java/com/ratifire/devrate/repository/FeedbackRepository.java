package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Feedback;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Feedback entity.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

  @Query("SELECT COUNT(f) > 0 FROM Feedback f WHERE f.user.id = :userId "
      + "AND f.createdAt > :oneMonthAgo")
  boolean existsFeedbackWithinLastMonth(@Param("userId") long userId,
      @Param("oneMonthAgo") LocalDateTime oneMonthAgo);
}
