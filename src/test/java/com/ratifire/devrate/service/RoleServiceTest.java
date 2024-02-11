package com.ratifire.devrate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ratifire.devrate.entity.Role;
import com.ratifire.devrate.exception.RoleNotFoundException;
import com.ratifire.devrate.repository.RoleRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the RoleService class. This class tests the behavior of the RoleService methods.
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
        .name("ROLE_USER")
        .build();
  }

  /**
   * Test method for retrieving a role by name. This method verifies that the RoleService correctly
   * retrieves a role by its name.
   */
  @Test
  public void testGetRoleByName() {
    when(roleRepository.findByName(any())).thenReturn(Optional.of(testUserRole));
    Role expectedRole = roleService.getRoleByName(any());
    assertEquals(expectedRole, testUserRole);
  }

  /**
   * Test method for retrieving a role by name when the role is not found. This method verifies that
   * the RoleService correctly throws a RoleNotFoundException when attempting to retrieve a role
   * that does not exist.
   */
  @Test
  public void testGetRoleByNameRoleNotFound() throws RoleNotFoundException {
    when(roleRepository.findByName(any())).thenThrow(RoleNotFoundException.class);
    assertThrows(RoleNotFoundException.class, () -> roleService.getRoleByName(any()));
  }
}
