package com.starlit.userservice.controller;

import com.starlit.userservice.dto.LoginRequest;
import com.starlit.userservice.dto.LoginResponse;
import com.starlit.userservice.dto.ProfileResponse;
import com.starlit.userservice.dto.ProfileUpdateRequest;
import com.starlit.userservice.dto.SignupRequest;
import com.starlit.userservice.dto.SignupResponse;
import com.starlit.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 REST API 컨트롤러.
 *
 * <pre>
 * POST /api/users/signup   → 회원가입 (201)
 * POST /api/users/login    → 로그인, JWT 발급 (200)
 * GET  /api/users/me       → 프로필 조회 (200, X-User-Id 필요)
 * PUT  /api/users/me       → 프로필 수정 (200, X-User-Id 필요)
 * </pre>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 회원가입. 이메일·닉네임 중복 시 409, 검증 실패 시 400. */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return userService.signup(request);
    }

    /** 로그인. 성공 시 JWT 토큰과 닉네임 반환. 인증 실패 시 401. */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /** 내 프로필 조회. Gateway가 JWT에서 추출한 X-User-Id 헤더 필요. */
    @GetMapping("/me")
    public ProfileResponse getProfile(@RequestHeader("X-User-Id") Long userId) {
        return userService.getProfile(userId);
    }

    /** 내 프로필 수정 (닉네임). 닉네임 중복 시 409. */
    @PutMapping("/me")
    public ProfileResponse updateProfile(@RequestHeader("X-User-Id") Long userId,
                                         @Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(userId, request);
    }
}
