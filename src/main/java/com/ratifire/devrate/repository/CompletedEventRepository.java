package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.CompletedEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on completed event entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface CompletedEventRepository extends JpaRepository<CompletedEvent, Long> {

  Optional<CompletedEvent> findByEventId(long eventId);
}