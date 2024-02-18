package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user in the system. A user is an entity with authentication
 * credentials and associated profile information.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_details")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Email
  @Size(max = 100)
  @Column(nullable = false, unique = true)
  private String email;

  @NotEmpty
  @Size(max = 100)
  @Column(nullable = false)
  private String firstName;

  @NotEmpty
  @Size(max = 100)
  @Column(nullable = false)
  private String lastName;

  @NotEmpty
  @Size(max = 100)
  @Column(nullable = false)
  private String country;

  @NotNull
  @Column(name = "is_subscribed", nullable = false)
  private boolean subscribed;

  @NotNull
  @Column(name = "is_verified", nullable = false)
  private boolean verified;

  @NotNull
  @Column(nullable = false)
  private LocalDateTime createdAt;
}
