package com.ratifire.devrate.entity;

import com.ratifire.devrate.enums.ContactType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contacts")
public class Contact {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotEmpty
  @Size(max = 100)
  @Enumerated(EnumType.STRING)
  private ContactType type;

  @NotEmpty
  @Size(max = 100)
  private String value;

  //TODO: Need to migrate migrate to the entity personal_info
  @NotEmpty
  @Column(name = "user_id", nullable = false)
  private long userId;

}
