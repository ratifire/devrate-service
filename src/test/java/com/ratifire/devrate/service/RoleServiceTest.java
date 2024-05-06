package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.enums.AccessLevel;
import com.ratifire.devrate.repository.RoleRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for the {@link RoleService}.
 */
@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private RoleService roleService;

  private Role testUserRole;

  /**
   * Initializes the test environment for the RegistrationControllerTest class. This method is
   * executed before each test method in the class.
   */
  @BeforeEach
  public void init() {
    testUserRole = Role.builder()
        .name(AccessLevel.USER.getRoleName())
        .build();
  }

  @Test
  public void testGetDefaultRole() {
    when(roleRepository.findByName(any())).thenReturn(Optional.of(testUserRole));
    Role expectedRole = roleService.getDefaultRole();
    assertEquals(expectedRole, testUserRole);
  }
}
