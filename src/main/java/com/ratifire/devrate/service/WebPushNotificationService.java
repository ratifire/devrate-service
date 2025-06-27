package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.NotificationDto;
import com.ratifire.devrate.entity.notification.webpush.WebPushSubscription;
import com.ratifire.devrate.repository.WebPushSubscriptionRepository;
import com.ratifire.devrate.util.JsonConverter;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for performing operations related to sending web push notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushNotificationService {

  @Value("${VAPID_PUBLIC_KEY}")
  private String publicKey;

  @Value("${VAPID_PRIVATE_KEY}")
  private String privateKey;

  @Value("${VAPID_SUBJECT}")
  private String subject;

  private final WebPushSubscriptionRepository repository;

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * Send notification by logged user.
   *
   * @param userId  current logged userId
   * @param message message that will be sent
   */
  @Transactional
  public void sendNotification(Long userId, NotificationDto message) {
    try {
      List<WebPushSubscription> subs = repository.findAllByUserId(userId);
      PushService pushService = new PushService(publicKey, privateKey, subject);
      String payload = JsonConverter.serialize(message);
      for (WebPushSubscription sub : subs) {
        try {
          sendNotification(sub, payload, pushService);
        } catch (Exception e) {
          log.error("Error while sending web push notification to user {}", userId, e);
        }
      }
    } catch (GeneralSecurityException e) {
      log.error("Error creating push service notification {}", userId, e);
    }
  }

  private void sendNotification(WebPushSubscription sub, String payload, PushService pushService)
      throws Exception {
    Notification notification = new Notification(sub.getEndpoint(), sub.getPublicKey(),
        sub.getAuth(), payload);

    HttpResponse resp = pushService.send(notification);
    int statusCode = resp.getStatusLine().getStatusCode();
    if (statusCode == 404 || statusCode == 410) {
      repository.deleteByEndpoint(sub.getEndpoint()); // remove inactive subscription
    } else if (statusCode > 299) {
      log.warn("Web push notification failed, status code {}. Message: {}", statusCode, payload);
    }
  }
}
