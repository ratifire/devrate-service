package com.ratifire.devrate.security.model;

public record UserInfo(
    String firstName,
    String lastName,
    String email,
    String subject,
    String provider
) {}