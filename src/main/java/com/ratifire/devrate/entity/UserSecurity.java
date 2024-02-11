package com.ratifire.devrate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

/**
 * Entity class representing the security information associated with a user in the system.
 * This entity stores sensitive information such as passwords and user roles.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_security")
public class UserSecurity {

    /**
     * The unique identifier for the user security information.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    /**
     * The password of the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The unique identifier of the user associated with this security information.
     */
    @Column(name = "user_id", nullable = false)
    private BigInteger userId;

    /**
     * The role identifier of the user associated with this security information.
     */
    @Column(name = "user_role_id", nullable = false)
    private BigInteger userRoleId;
}
