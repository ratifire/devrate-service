package com.ratifire.devrate.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * Configuration class for WebSocket messaging.
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig extends AbstractWebSocketHandler
        implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/chat").setAllowedOriginPatterns("*")
            .withSockJS()
            .setHeartbeatTime(10000);
    registry.addEndpoint("/chat")
            .setAllowedOriginPatterns("*");
    registry.addEndpoint("/notifications-ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
            .setHeartbeatTime(10000);
    registry.addEndpoint("/notifications-ws").setAllowedOriginPatterns("*");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }
}
