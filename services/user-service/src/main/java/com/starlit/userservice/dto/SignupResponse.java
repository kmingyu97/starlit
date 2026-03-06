package com.starlit.userservice.dto;

public record SignupResponse(
        Long id,
        String email,
        String nickname
) {
}
