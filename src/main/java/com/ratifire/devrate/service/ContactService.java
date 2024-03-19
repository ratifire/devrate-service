package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.exception.ContactNotFoundException;
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.ContactMapper;
import com.ratifire.devrate.repository.ContactRepository;
import com.ratifire.devrate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing contacts. This class handles the business logic for creating,
 * retrieving, updating, and deleting contacts associated with users.
 */
@Service
@RequiredArgsConstructor
public class ContactService {

  private final UserRepository userRepository;

  private final ContactRepository contactRepository;

  private final ContactMapper contactMapper;

  /**
   * Retrieves a contact by its ID and converts it to a ContactDto. If the contact is not found, a
   * ContactNotFoundException is thrown.
   *
   * @param id the ID of the contact to retrieve
   * @return the ContactDto corresponding to the specified ID
   * @throws ContactNotFoundException if no contact is found for the given ID
   */
  @Transactional(readOnly = true)
  public ContactDto findById(long id) {
    return contactRepository.findById(id)
        .map(contactMapper::toDto)
        .orElseThrow(() -> new ContactNotFoundException("Contact with id " + id + " not found"));
  }

  /**
   * Creates a new contact for a user identified by userId.
   *
   * @param userId     the ID of the user to whom the contact belongs
   * @param contactDto the contact information to create
   * @return the created ContactDto
   * @throws UserNotFoundException if the user with the given ID does not exist
   */
  @Transactional
  public ContactDto create(long userId, ContactDto contactDto) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException("User with id " + userId + " not found");
    }
    Contact contact = contactMapper.toEntity(contactDto);
    contact.setUserId(userId);
    contactRepository.save(contact);
    return contactMapper.toDto(contact);
  }

  /**
   * Updates an existing contact identified by ID with new information provided in contactDto.
   *
   * @param id         the ID of the contact to update
   * @param contactDto the new contact information
   * @return the updated ContactDto
   * @throws ContactNotFoundException if no contact is found for the given ID
   */
  @Transactional
  public ContactDto update(long id, ContactDto contactDto) {
    Contact contact = contactRepository.findById(id)
        .orElseThrow(() -> new ContactNotFoundException("Contact with id " + id + " not found"));
    contactMapper.toUpdate(contactDto, contact);
    contactRepository.save(contact);
    return contactMapper.toDto(contact);
  }

  /**
   * Deletes a contact by its ID.
   *
   * @param id the ID of the contact to delete
   */
  @Transactional
  public void delete(long id) {
    if (!contactRepository.existsById(id)) {
      throw new ContactNotFoundException("Contact with id " + id + " not found");
    }
    contactRepository.deleteById(id);
  }

}
