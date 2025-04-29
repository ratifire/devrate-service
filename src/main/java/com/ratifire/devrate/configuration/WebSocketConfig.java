package com.ratifire.devrate.configuration;

import com.ratifire.devrate.exception.EmailNotFoundException;
import com.ratifire.devrate.util.WebSocketSessionRegistry;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
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
        implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

  private final WebSocketSessionRegistry sessionRegistry;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(this, "/ws/notifications").setAllowedOrigins("*");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
    registry.addEndpoint("/chat").setAllowedOriginPatterns("*");
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    String email = extractEmail(session);
    sessionRegistry.registerSession(email, session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session,
      @NonNull CloseStatus closeStatus) {
    String email = extractEmail(session);
    sessionRegistry.closeRemoveSession(email, session);
  }

  /**
   * Extracts the email associated with the given WebSocket session.
   *
   * @param session the WebSocket session
   * @return the email associated with the session
   * @throws EmailNotFoundException if the email is not found in the session
   */
  private String extractEmail(WebSocketSession session) {
    Principal principal = session.getPrincipal();
    if (principal == null) {
      throw new EmailNotFoundException("Login not found.");
    }
    return principal.getName();
  }
}
