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
import com.ratifire.devrate.exception.UserNotFoundException;
import com.ratifire.devrate.mapper.ContactMapper;
import com.ratifire.devrate.repository.ContactRepository;
import com.ratifire.devrate.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

  @InjectMocks
  private ContactService contactService;

  @Mock
  private ContactRepository contactRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ContactMapper contactMapper;

  private ContactDto contactDto;

  private Contact contact;

  private final long USER_ID = 1;

  private final long ID = 1;

  @BeforeEach
  void setUp() {
    contactDto = new ContactDto();
    contact = new Contact();
  }

  @Test
  void findByIdTest() {
    when(contactRepository.findById(ID)).thenReturn(Optional.of(contact));
    when(contactMapper.toDto(contact)).thenReturn(contactDto);

    ContactDto result = contactService.findById(ID);

    assertEquals(contactDto, result);
    verify(contactRepository).findById(ID);
    verify(contactMapper).toDto(contact);
  }

  @Test
  void findByIdThrowContactNotFoundExceptionTest() {
    when(contactRepository.findById(ID)).thenReturn(Optional.empty());

    assertThrows(ContactNotFoundException.class, () -> contactService.findById(ID));
  }

  @Test
  void createTest() {
    when(userRepository.existsById(USER_ID)).thenReturn(true);
    when(contactMapper.toEntity(contactDto)).thenReturn(contact);
    when(contactRepository.save(contact)).thenReturn(contact);
    when(contactMapper.toDto(contact)).thenReturn(contactDto);

    ContactDto contactDtoCreated = contactService.create(USER_ID, contactDto);

    assertEquals(contactDto, contactDtoCreated);
    verify(userRepository).existsById(USER_ID);
    verify(contactMapper).toEntity(contactDto);
    verify(contactRepository).save(contact);
    verify(contactMapper).toDto(contact);
  }

  @Test
  void createThrowUserNotFoundExceptionTest() {
    when(userRepository.existsById(USER_ID)).thenReturn(false);

    assertThrows(UserNotFoundException.class, () -> contactService.create(USER_ID, contactDto));
  }


  @Test
  void updateTest() {
    when(contactRepository.findById(ID)).thenReturn(Optional.of(contact));
    doNothing().when(contactMapper).toUpdate(any(ContactDto.class), any(Contact.class));
    when(contactRepository.save(any(Contact.class))).thenReturn(contact);
    when(contactMapper.toDto(any(Contact.class))).thenReturn(contactDto);

    ContactDto contactDtoUpdated = contactService.update(ID, contactDto);

    assertEquals(contactDto, contactDtoUpdated);
    verify(contactRepository).findById(ID);
    verify(contactRepository).save(any(Contact.class));
    verify(contactMapper).toUpdate(any(ContactDto.class), any(Contact.class));
    verify(contactMapper).toDto(any(Contact.class));
  }


  @Test
  void updateThrowContactNotFoundExceptionTest() {
    when(contactRepository.findById(ID)).thenReturn(Optional.empty());

    assertThrows(ContactNotFoundException.class, () -> contactService.update(ID, contactDto));
  }

  @Test
  void deleteTest() {
    when(contactRepository.existsById(ID)).thenReturn(true);

    contactService.delete(ID);

    verify(contactRepository).existsById(ID);
    verify(contactRepository).deleteById(ID);
  }

  @Test
  void deleteThrowContactNotFoundExceptionTest() {
    when(contactRepository.existsById(ID)).thenReturn(false);

    assertThrows(ContactNotFoundException.class, () -> contactService.delete(ID));
  }

}
