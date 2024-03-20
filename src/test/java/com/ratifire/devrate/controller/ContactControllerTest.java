package com.ratifire.devrate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.configuration.SecurityConfiguration;
import com.ratifire.devrate.dto.ContactDto;
import com.ratifire.devrate.enums.ContactType;
import com.ratifire.devrate.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for the ContactController class.
 */
@WebMvcTest(ContactController.class)
@Import(SecurityConfiguration.class)
class ContactControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ContactService contactService;

  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private ObjectMapper objectMapper;

  private final long CONTACT_ID = 1;

  private final long USER_ID = 1;

  private ContactDto contactDto;

  private ContactDto contactDtoUpdated;

  @BeforeEach
  public void setUp() {
    contactDto = ContactDto.builder()
        .type(ContactType.PHONE_NUMBER)
        .value("123456789025")
        .build();

    contactDtoUpdated = ContactDto.builder()
        .type(ContactType.PHONE_NUMBER)
        .value("123400000000")
        .build();

  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void findByIdTest() throws Exception {
    when(contactService.findById(CONTACT_ID)).thenReturn(contactDto);
    mockMvc.perform(get("/contacts/{id}", CONTACT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.type").value(contactDto.getType().toString()))
        .andExpect(jsonPath("$.value").value(contactDto.getValue()));
    verify(contactService, times(1)).findById(anyLong());
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void createTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(contactDto);
    when(contactService.create(anyLong(), any(ContactDto.class))).thenReturn(contactDto);
    mockMvc.perform(post("/contacts/{userId}", USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.type").value(contactDto.getType().toString()))
        .andExpect(jsonPath("$.value").value(contactDto.getValue()));
    verify(contactService, times(1)).create(anyLong(), any(ContactDto.class));
  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void updateTest() throws Exception {
    String requestBody = objectMapper.writeValueAsString(contactDtoUpdated);
    when(contactService.update(anyLong(), any(ContactDto.class))).thenReturn(contactDtoUpdated);
    mockMvc.perform(put("/contacts/{id}", CONTACT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.type").value(contactDtoUpdated.getType().toString()))
        .andExpect(jsonPath("$.value").value(contactDtoUpdated.getValue()));
    verify(contactService, times(1)).update(anyLong(), any(ContactDto.class));

  }

  @Test
  @WithMockUser(username = "test@gmail.com", password = "test", roles = "USER")
  void deleteTest() throws Exception {
    mockMvc.perform(delete("/contacts/{id}", CONTACT_ID))
        .andExpect(status().isOk());
  }

}
