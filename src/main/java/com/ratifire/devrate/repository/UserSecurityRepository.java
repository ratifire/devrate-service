package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * Repository interface for performing CRUD operations on UserSecurity entities.
 * This interface provides methods for accessing and managing UserSecurity entities in the database.
 */
@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurity, BigInteger> {

}
