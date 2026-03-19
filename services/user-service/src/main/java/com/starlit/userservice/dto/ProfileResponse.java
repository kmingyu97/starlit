package com.starlit.userservice.dto;

import java.time.LocalDateTime;

public record ProfileResponse(Long id, String email, String nickname, LocalDateTime createdAt) {
}
