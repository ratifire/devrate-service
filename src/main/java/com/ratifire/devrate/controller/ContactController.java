package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling contact-related endpoints. Provides CRUD operations for managing
 * contacts associated with users.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/contacts")
public class ContactController {

  private final ContactService contactService;

  /**
   * Retrieves a contact by its ID.
   *
   * @param id the ID of the contact to retrieve
   * @return ContactDto representing the contact entity.
   */
  @GetMapping("/{id}")
  public ContactDto getById(@PathVariable long id) {
    return contactService.findById(id);
  }

  /**
   * Creates a new contact for a user.
   *
   * @param userId     the ID of the user to associate the new contact with
   * @param contactDto the contact data to create
   * @return contactDto the created contact data
   */
  @PostMapping("/{userId}")
  public ContactDto create(@PathVariable long userId, @RequestBody ContactDto contactDto) {
    return contactService.create(userId, contactDto);
  }

  /**
   * Updates an existing contact.
   *
   * @param id         the ID of the contact to update
   * @param contactDto the updated contact data
   * @return contactDto the updated contact data
   */
  @PutMapping("/{id}")
  public ContactDto update(@PathVariable long id, @RequestBody ContactDto contactDto) {
    return contactService.update(id, contactDto);
  }

  /**
   * Deletes a contact by its ID.
   *
   * @param id the ID of the contact to delete
   */
  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    contactService.delete(id);
  }

}
