package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.WebPushSubscriptionDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.notification.webpush.WebPushSubscription;
import com.ratifire.devrate.repository.WebPushSubscriptionRepository;
import com.ratifire.devrate.security.helper.UserContextProvider;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for performing operations related to WebPushSubscription entity.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushSubscriptionService {

  private final WebPushSubscriptionRepository repository;
  private final UserService userService;
  private final UserContextProvider userContextProvider;

  /**
   * Save subscription.
   *
   * @param dto from client
   */
  public void saveSubscription(WebPushSubscriptionDto dto) {
    long currentUserId = userContextProvider.getAuthenticatedUserId();

    Optional<WebPushSubscription> existingSub = repository.findByEndpoint(dto.getEndpoint());
    if (existingSub.isPresent()) {
      WebPushSubscription sub = existingSub.get();
      if (sub.getUser().getId() == currentUserId) {
        // already linked to the current user do nothing
        return;
      }
    }

    User currentUser = userService.findById(currentUserId);
    repository.save(WebPushSubscription.builder()
        .endpoint(dto.getEndpoint())
        .publicKey(dto.getKeys().get("p256dh"))
        .auth(dto.getKeys().get("auth"))
        .createdAt(LocalDateTime.now(ZoneOffset.UTC))
        .user(currentUser)
        .build());
  }

  /**
   * Delete subscription from the DB.
   *
   * @param endpoint endpoint of the subscription
   */
  @Transactional
  public void deleteSubscription(String endpoint) {
    repository.findByEndpoint(endpoint).ifPresent(sub -> repository.deleteByEndpoint(endpoint));
  }
}
