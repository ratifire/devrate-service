package com.ratifire.devrate.repository;

import com.ratifire.devrate.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Contact repository.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

}
