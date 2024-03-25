package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on User Personal Info entities.
 * This interface provides methods for accessing and managing User Personal Info entities in the
 * database.
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}
