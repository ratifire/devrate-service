package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.repository.UserSecurityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSecurityServiceTest {

    @Mock
    private UserSecurityRepository userSecurityRepository;

    @InjectMocks
    private UserSecurityService userSecurityService;

    private UserSecurity testUserSecurity;

    @BeforeEach
    public void init() {
        testUserSecurity = UserSecurity.builder()
                .password("TestPassword123")
                .build();
    }

    @Test
    public void givenUserSecurityObject_whenSave_thenReturnUserSecurityObject() {
        when(userSecurityRepository.save(any())).thenReturn(testUserSecurity);
        UserSecurity expectedUserSecurity = userSecurityService.save(UserSecurity.builder().build());
        assertEquals(expectedUserSecurity, testUserSecurity);
    }
}
