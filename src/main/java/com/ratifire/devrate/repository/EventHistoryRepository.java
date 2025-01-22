package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.EventHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on event history entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {

  Optional<EventHistory> findByEventId(long eventId);
}