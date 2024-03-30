package com.ratifire.devrate.service;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.exception.ContactNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing contacts. This class handles the business logic for creating,
 * retrieving, updating, and deleting contacts associated with users.
 */
@Service
@RequiredArgsConstructor
public class ContactService {

  private final ContactRepository contactRepository;

  private final DataMapper<ContactDto, Contact> contactMapper;

  /**
   * Retrieves a contact by its ID and converts it to a ContactDto. If the contact is not found, a
   * ContactNotFoundException is thrown.
   *
   * @param id the ID of the contact to retrieve
   * @return the ContactDto corresponding to the specified ID
   * @throws ContactNotFoundException if no contact is found for the given ID
   */
  public ContactDto findById(long id) {
    return contactRepository.findById(id)
        .map(contactMapper::toDto)
        .orElseThrow(() -> new ContactNotFoundException(id));
  }

  /**
   * Creates a new contact for a user identified by userId.
   *
   * @param userId     the ID of the user to whom the contact belongs
   * @param contactDto the contact information to create
   * @return the created ContactDto
   */
  public ContactDto create(long userId, ContactDto contactDto) {
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
  public ContactDto update(long id, ContactDto contactDto) {
    Contact contact = contactRepository.findById(id)
        .orElseThrow(() -> new ContactNotFoundException(id));
    contactMapper.updateEntity(contactDto, contact);
    contactRepository.save(contact);
    return contactMapper.toDto(contact);
  }

  /**
   * Deletes a contact by its ID.
   *
   * @param id the ID of the contact to delete
   */
  public void delete(long id) {
    if (!contactRepository.existsById(id)) {
      throw new ContactNotFoundException(id);
    }
    contactRepository.deleteById(id);
  }

}
