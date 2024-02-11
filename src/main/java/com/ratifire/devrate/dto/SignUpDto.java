package com.ratifire.devrate.dto;

import com.ratifire.devrate.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing the user details required for sign up or registration.
 * This DTO encapsulates the necessary information needed to create a new user account.
 */
@Data
@Builder
public class SignUpDto {

    /**
     * The email chosen by the user for their account.
     */
    private String email;

    /**
     * The first name inputted by user
     */
    private String firstName;

    /**
     * The last name inputted by user
     */
    private String lastName;

    /**
     * The country chosen by the user for their account.
     */
    private String country;

    /**
     * Flag indicating whether the user has subscribed to newsletters or updates.
     */
    private boolean isSubscribed;

    /**
     * Flag indicating whether the user's email address has been verified.
     */
    private boolean isVerified;

    /**
     * The password chosen by the user for their account.
     */
    private String password;

    /**
     * Converts this SignUpDto object into a User object.
     *
     * @return A User object with the same details as this SignUpDto.
     */
    public User toUser() {
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .country(country)
                .isSubscribed(isSubscribed)
                .isVerified(isVerified())
                .created_at(LocalDateTime.now())
                .build();
    }
}
