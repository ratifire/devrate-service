package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link EmailConfirmationCode} entities in the database.
 * Extends {@link JpaRepository} to inherit basic CRUD operations.
 */
@Repository
public interface EmailConfirmationCodeRepository
        extends JpaRepository<EmailConfirmationCode, Long> {

  /**
   * Retrieves an {@link EmailConfirmationCode} by user ID.
   *
   * @param userId The ID of the user associated with the email confirmation code.
   * @return The optional of the {@link EmailConfirmationCode} entity with the specified name.
   */
  Optional<EmailConfirmationCode> findByUserId(long userId);

  /**
   * Retrieves an {@link EmailConfirmationCode} by code.
   *
   * @param code The code associated with the email confirmation code.
   * @return The optional of the {@link EmailConfirmationCode} entity with the specified code.
   */
  Optional<EmailConfirmationCode> findByCode(String code);

  /**
   * Deletes {@link EmailConfirmationCode} entities by user ID.
   *
   * @param userId The ID of the user associated with the email confirmation code.
   */
  void deleteByUserId(Long userId);
}
