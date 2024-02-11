package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user in the system. A user is an entity with authentication
 * credentials and associated profile information.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_details")
public class User {

  /**
   * The unique identifier for the user.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /**
   * The email address of the user.
   */
  @Column(nullable = false, unique = true)
  private String email;

  /**
   * The first name of the user.
   */
  @Column(nullable = false)
  private String firstName;

  /**
   * The last name of the user.
   */
  @Column(nullable = false)
  private String lastName;

  /**
   * The country of the user.
   */
  @Column(nullable = false)
  private String country;

  /**
   * Flag indicating whether the user has subscribed to newsletters or updates.
   */
  private boolean isSubscribed;

  /**
   * Flag indicating whether the user's email address has been verified.
   */
  private boolean isVerified;

  /**
   * The timestamp representing when the user account was created.
   */
  @Column(nullable = false)
  private LocalDateTime createdAt;
}
