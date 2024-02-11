package com.ratifire.devrate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

/**
 * Entity class representing a role in the system.
 * A role defines a set of permissions or privileges granted to users.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_role")
public class Role {

    /**
     * The unique identifier for the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    /**
     * The name of the role.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * A brief description of the role.
     */
    private String description;
}
