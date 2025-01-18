package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Event entity.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface EventRepository extends JpaRepository<Event, Long> {

}