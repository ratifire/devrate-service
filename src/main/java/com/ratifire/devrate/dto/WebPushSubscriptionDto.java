package com.ratifire.devrate.dto;

import java.util.Map;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the web push subscription.
 */
@Data
public class WebPushSubscriptionDto {
  private String endpoint;
  private Map<String, String> keys;
}
