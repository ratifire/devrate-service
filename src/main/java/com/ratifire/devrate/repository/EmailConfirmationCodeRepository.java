package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.EmailConfirmationCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on EmailConfirmationCode entities.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface EmailConfirmationCodeRepository extends
    JpaRepository<EmailConfirmationCode, Long> {

  Optional<EmailConfirmationCode> findByCode(String code);

  Optional<EmailConfirmationCode> findByUserId(Long userId);

  void deleteByUserId(Long userId);
}