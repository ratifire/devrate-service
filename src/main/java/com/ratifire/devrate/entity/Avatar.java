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
 * Entity class representing a user avatar within the application. It stores information about the
 * avatar's storage path, file name, and the associated user ID.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_avatars")
public class Avatar {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String path;

  private String fileName;

  @Column(name = "user_id", nullable = false)
  private long userId;
}
