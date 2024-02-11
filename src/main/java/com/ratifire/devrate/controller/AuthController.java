package com.ratifire.devrate.controller;

import com.ratifire.devrate.dto.SignUpDto;
import com.ratifire.devrate.entity.User;
import com.ratifire.devrate.entity.UserSecurity;
import com.ratifire.devrate.exception.UserAlreadyExistException;
import com.ratifire.devrate.service.RoleService;
import com.ratifire.devrate.service.UserSecurityService;
import com.ratifire.devrate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class responsible for handling authentication-related requests.
 * This controller provides endpoints for user login, logout, and registration.
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Service responsible for user management operations.
     */
    private final UserService userService;

    /**
     * Service responsible for role management operations.
     */
    private final RoleService roleService;

    /**
     * Service responsible for user security management operations.
     */
    private final UserSecurityService userSecurityService;

    /**
     * Endpoint for user registration.
     * Accepts POST requests with user details and registers a new user.
     *
     * @param signUpDto DTO containing new user's details such as username, password, etc.
     * @return - ResponseEntity with HTTP status 200 (OK)
     * and newly created user's details upon successful registration.
     * - Throws UserAlreadyExistException with HTTP status 409 (Conflict)
     * if inputted email is already registered.
     */
    @PostMapping("/signup")
    public ResponseEntity<User> registerUser(@RequestBody SignUpDto signUpDto) {
        if (userService.isExistByEmail(signUpDto.getEmail())) {
            throw new UserAlreadyExistException("User is already registered!");
        }

        User user = userService.save(signUpDto.toUser());

        UserSecurity userSecurity = UserSecurity.builder()
                .password(signUpDto.getPassword())
                .userId(user.getId())
                .userRoleId(roleService.getRoleByName("ROLE_USER").getId())
                .build();
        userSecurityService.save(userSecurity);

        return ResponseEntity.ok(user);
    }
}
