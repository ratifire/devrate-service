package com.ratifire.devrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing the security information associated with a user in the system. This
 * entity stores sensitive information such as passwords and user roles.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_security")
public class UserSecurity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String password;

  @Column(name = "user_id", nullable = false)
  private long userId;

  @Column(name = "user_role_id", nullable = false)
  private long userRoleId;
}
