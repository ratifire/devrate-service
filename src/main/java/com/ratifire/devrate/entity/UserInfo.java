package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing an entity for storing user personal information.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_info")
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Size(max = 100)
  @Column(nullable = false)
  private String firstName;

  @Size(max = 100)
  @Column(nullable = false)
  private String lastName;

  @Size(max = 50)
  private String position;

  @Size(max = 100)
  @Column(nullable = false)
  private String country;

  @Size(max = 100)
  private String state;

  @Size(max = 100)
  @Column(nullable = false)
  private String city;

  @Column(name = "is_subscribed", nullable = false)
  private boolean subscribed;

  private String description;

  @NotNull
  @Column(name = "user_id", nullable = false)
  private long userId;
}
