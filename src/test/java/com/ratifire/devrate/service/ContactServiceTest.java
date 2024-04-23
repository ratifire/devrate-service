package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.entity.Contact;
import com.ratifire.devrate.exception.ContactNotFoundException;
import com.ratifire.devrate.mapper.DataMapper;
import com.ratifire.devrate.repository.ContactRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the ContactService class.
 */
@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

  @InjectMocks
  private ContactService contactService;

  @Mock
  private ContactRepository contactRepository;

  @Mock
  private DataMapper<ContactDto, Contact> contactMapper;

  private ContactDto contactDto;

  private Contact contact;

  private long userId = 1;

  private long id = 1;

  @BeforeEach
  void setUp() {
    contactDto = new ContactDto();
    contact = new Contact();
  }

  @Test
  void findByIdTest() {
    when(contactRepository.findById(id)).thenReturn(Optional.of(contact));
    when(contactMapper.toDto(contact)).thenReturn(contactDto);

    ContactDto result = contactService.findById(id);

    assertEquals(contactDto, result);
    verify(contactRepository).findById(id);
    verify(contactMapper).toDto(contact);
  }

  @Test
  void findByIdThrowContactNotFoundExceptionTest() {
    when(contactRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ContactNotFoundException.class, () -> contactService.findById(id));
  }

  @Test
  void createTest() {
    when(contactMapper.toEntity(contactDto)).thenReturn(contact);
    when(contactRepository.save(contact)).thenReturn(contact);
    when(contactMapper.toDto(contact)).thenReturn(contactDto);

    ContactDto contactDtoCreated = contactService.create(userId, contactDto);

    assertEquals(contactDto, contactDtoCreated);
    verify(contactMapper).toEntity(contactDto);
    verify(contactRepository).save(contact);
    verify(contactMapper).toDto(contact);
  }

  @Test
  void updateTest() {
    when(contactRepository.findById(id)).thenReturn(Optional.of(contact));
    when(contactMapper.updateEntity(any(), any())).thenReturn(contact);
    when(contactRepository.save(any(Contact.class))).thenReturn(contact);
    when(contactMapper.toDto(any(Contact.class))).thenReturn(contactDto);

    ContactDto contactDtoUpdated = contactService.update(id, contactDto);

    assertEquals(contactDto, contactDtoUpdated);
    verify(contactRepository).findById(id);
    verify(contactRepository).save(any(Contact.class));
    verify(contactMapper).updateEntity(any(ContactDto.class), any(Contact.class));
    verify(contactMapper).toDto(any(Contact.class));
  }

  @Test
  void updateThrowContactNotFoundExceptionTest() {
    when(contactRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ContactNotFoundException.class, () -> contactService.update(id, contactDto));
  }

  @Test
  void deleteTest() {
    when(contactRepository.existsById(id)).thenReturn(true);

    contactService.delete(id);

    verify(contactRepository).existsById(id);
    verify(contactRepository).deleteById(id);
  }

  @Test
  void deleteThrowContactNotFoundExceptionTest() {
    when(contactRepository.existsById(id)).thenReturn(false);

    assertThrows(ContactNotFoundException.class, () -> contactService.delete(id));
  }

}
