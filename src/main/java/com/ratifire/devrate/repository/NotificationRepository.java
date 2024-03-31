package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Notification entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findAllByUser_EmailOrderByCreatedAtDesc(String email);
}
