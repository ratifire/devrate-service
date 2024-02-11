package com.ratifire.devrate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private static final String END_POINT_PATH = "/api/auth";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private UserSecurityService userSecurityService;

    private SignUpDto signUpDto;

    @BeforeEach
    public void init() {
        signUpDto = SignUpDto.builder()
                .email("test@gmail.com")
                .firstName("Test first name")
                .lastName("Test last name")
                .country("Test country")
                .isVerified(true)
                .isSubscribed(true)
                .password("TestPassword123")
                .build();
    }

    @Test
    public void testSignUpShouldReturn200OK() throws Exception {
        Mockito.when(userService.save(any())).thenReturn(User.builder().build());
        Mockito.when(userSecurityService.save(any())).thenReturn(UserSecurity.builder().build());

        mockMvc.perform(post(END_POINT_PATH + "/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testSignUpShouldReturn409Conflict() throws Exception {
        Mockito.when(userService.isExistByEmail(any())).thenReturn(true);

        mockMvc.perform(post(END_POINT_PATH + "/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}
