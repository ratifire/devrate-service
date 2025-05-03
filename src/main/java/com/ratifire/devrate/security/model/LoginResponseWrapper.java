package com.ratifire.devrate.security.model;

import com.ratifire.devrate.dto.LoginResponseDto;

public record LoginResponseWrapper(String accessToken,
                                   String refreshToken,
                                   String idToken,
                                   LoginResponseDto loginResponse) {

}