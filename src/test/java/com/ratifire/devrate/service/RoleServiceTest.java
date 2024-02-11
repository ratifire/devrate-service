package com.ratifire.devrate.service;

import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.exception.RoleNotFoundException;
import com.ratifire.devrate.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role testUserRole;

    @BeforeEach
    public void init() {
        testUserRole = Role.builder()
                .name("ROLE_USER")
                .build();
    }

    @Test
    public void testGetRoleShouldReturnRole() {
        when(roleRepository.findByName(any())).thenReturn(Optional.of(testUserRole));
        Role expectedRole = roleService.getRoleByName(any());
        assertEquals(expectedRole, testUserRole);
    }

    @Test
    public void testGetRoleShouldThrowNotFoundException() {
        when(roleRepository.findByName(any())).thenThrow(RoleNotFoundException.class);
        assertThrows(RoleNotFoundException.class, () -> roleService.getRoleByName(any()));
    }
}
