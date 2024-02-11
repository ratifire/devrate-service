package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void init() {
        testUser = User.builder()
                .email("test@gmail.com")
                .firstName("Test first name")
                .lastName("Test last name")
                .country("Test country")
                .isVerified(true)
                .isSubscribed(true)
                .created_at(LocalDateTime.now())
                .build();
    }

    @Test
    public void givenUserObject_whenSave_thenReturnUserObject() {
        when(userRepository.save(any())).thenReturn(testUser);
        User expectedUser = userService.save(User.builder().build());
        assertEquals(expectedUser, testUser);
    }

    @Test
    public void testUserExistsByEmail_ExistingEmail_ReturnsTrue() {
        String existingEmail = "existing@example.com";
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);
        boolean isExist = userService.isExistByEmail(existingEmail);
        assertTrue(isExist);
    }

    @Test
    public void testUserExistsByEmail_ExistingEmail_ReturnsFalse() {
        String existingEmail = "existing@example.com";
        when(userRepository.existsByEmail(existingEmail)).thenReturn(false);
        boolean isExist = userService.isExistByEmail(existingEmail);
        assertFalse(isExist);
    }
}
