package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Feedback;
import com.ratifire.devrate.entity.User;
import java.util.Optional;
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

  @Query("SELECT f FROM Feedback f WHERE f.user = :user ORDER BY f.createdAt DESC LIMIT 1")
  Optional<Feedback> findLatestFeedbackByUser(@Param("user") User user);
}
