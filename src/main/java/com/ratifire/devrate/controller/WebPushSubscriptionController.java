package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.WebPushSubscriptionDto;
import com.ratifire.devrate.service.WebPushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling web push subscriptions.
 */
@RestController
@RequestMapping("/web-subscription/")
@RequiredArgsConstructor
public class WebPushSubscriptionController {

  private final WebPushSubscriptionService subscriptionService;

  @PostMapping("/subscribe")
  public void subscribe(@RequestBody WebPushSubscriptionDto dto) {
    subscriptionService.saveSubscription(dto);
  }

  @PostMapping("/unsubscribe")
  public void unsubscribe(@RequestBody WebPushSubscriptionDto dto) {
    subscriptionService.deleteSubscription(dto.getEndpoint());
  }
}
