package com.ratifire.devrate.jwt.repository;

import com.ratifire.devrate.jwt.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * test.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface  RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  RefreshToken findByToken(String token);

  void deleteByToken(String token);

  boolean existsByToken(String token);
}