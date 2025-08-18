package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.notification.webpush.WebPushSubscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on web push subscription entity.
 */
@Repository
public interface WebPushSubscriptionRepository extends JpaRepository<WebPushSubscription, Long> {

  Optional<WebPushSubscription> findByEndpointAndUser_Id(String endpoint, long userId);

  void deleteByEndpoint(String endpoint);

  List<WebPushSubscription> findAllByUserId(Long userId);
}
