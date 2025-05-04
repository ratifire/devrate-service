package com.ratifire.devrate.entity;

import com.ratifire.devrate.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entity class representing a notification.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "JSONB")
  private String payload;

  @Column(nullable = false)
  private NotificationType type;

  @Column(name = "is_read", nullable = false)
  private boolean read;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
