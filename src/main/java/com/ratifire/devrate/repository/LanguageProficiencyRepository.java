package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.LanguageProficiency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Language proficiency entities. This
 * interface provides methods for accessing and managing Language proficiency entities in the
 * database.
 */
@Repository
public interface LanguageProficiencyRepository extends JpaRepository<LanguageProficiency, Long> {

}
