package com.ratifire.devrate.entity;

import com.ratifire.devrate.enums.LanguageLevel;
import com.ratifire.devrate.enums.LanguageName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Language entity represents a language known by a user.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "languages")
public class Language {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Enumerated(EnumType.STRING)
  private LanguageName name;

  @Enumerated(EnumType.STRING)
  private LanguageLevel level;

  @Column(name = "user_id", nullable = false)
  private long userId;

}
