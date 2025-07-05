package com.ratifire.devrate.security.model;

import com.ratifire.devrate.dto.LoginResponseDto;

/**
 * Wrapper record for encapsulating authentication tokens and login response details.
 *
 * @param accessToken   the JWT access token
 * @param refreshToken  the JWT refresh token
 * @param idToken       the JWT ID token
 * @param loginResponse the login response data transfer object
 */
public record LoginResponseWrapper(String accessToken,
                                   String refreshToken,
                                   String idToken,
                                   LoginResponseDto loginResponse) {

}