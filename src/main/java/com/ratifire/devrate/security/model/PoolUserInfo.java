package com.ratifire.devrate.security.model;

public record PoolUserInfo(
    String firstName,
    String lastName,
    String email,
    String subject,
    String provider,
    String linkedRecord,
    String cognitoUsername
) {}