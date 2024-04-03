package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Language entities. This interface provides
 * methods for accessing and managing Language entities in the database.
 */
@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

}
