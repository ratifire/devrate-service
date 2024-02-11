package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * Repository interface for performing CRUD operations on User entities.
 * This interface provides methods for accessing and managing User entities in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {

    /**
     * Checks if a user entity with the given email address exists.
     *
     * @param email The email address to check for existence.
     * @return True if a user with the specified email address exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
