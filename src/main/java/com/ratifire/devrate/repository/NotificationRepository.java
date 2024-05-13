package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Notification entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
