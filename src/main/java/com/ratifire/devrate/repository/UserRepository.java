package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on User entities. This interface provides
 * methods for accessing and managing User entities in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);
}
