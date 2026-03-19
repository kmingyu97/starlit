package com.starlit.userservice.controller;

import tools.jackson.databind.ObjectMapper;
import com.starlit.userservice.common.exception.CustomException;
import com.starlit.userservice.common.exception.ErrorCode;
import com.starlit.userservice.dto.LoginRequest;
import com.starlit.userservice.dto.LoginResponse;
import com.starlit.userservice.dto.SignupRequest;
import com.starlit.userservice.dto.SignupResponse;
import com.starlit.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users/signup - 회원가입 성공 시 201 반환")
    void signup_success() throws Exception {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "tester");
        SignupResponse response = new SignupResponse(1L, "test@example.com", "tester");

        given(userService.signup(any(SignupRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickname").value("tester"));
    }

    @Test
    @DisplayName("POST /api/users/signup - 이메일 중복 시 409 반환")
    void signup_duplicateEmail() throws Exception {
        // given
        SignupRequest request = new SignupRequest("dup@example.com", "Password1!", "tester");

        given(userService.signup(any(SignupRequest.class)))
                .willThrow(new CustomException(ErrorCode.DUPLICATE_EMAIL));

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("POST /api/users/signup - 이메일 형식 잘못된 경우 400 반환")
    void signup_invalidEmail() throws Exception {
        // given
        SignupRequest request = new SignupRequest("not-an-email", "Password1!", "tester");

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/users/signup - 비밀번호가 빈값이면 400 반환")
    void signup_blankPassword() throws Exception {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "", "tester");

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/users/signup - 닉네임이 빈값이면 400 반환")
    void signup_blankNickname() throws Exception {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "");

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/users/signup - 비밀번호가 8자 미만이면 400 반환")
    void signup_shortPassword() throws Exception {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Pass1!", "tester");

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // === 로그인 API 테스트 ===

    @Test
    @DisplayName("POST /api/users/login - 로그인 성공 시 200 + 토큰 반환")
    void login_success() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "Password1!");
        LoginResponse response = new LoginResponse("jwt-token", "tester");

        given(userService.login(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.nickname").value("tester"));
    }

    @Test
    @DisplayName("POST /api/users/login - 잘못된 인증정보 시 401 반환")
    void login_invalidCredentials() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "WrongPass1!");

        given(userService.login(any(LoginRequest.class)))
                .willThrow(new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("POST /api/users/login - 이메일 빈값이면 400 반환")
    void login_blankEmail() throws Exception {
        // given
        LoginRequest request = new LoginRequest("", "Password1!");

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/users/login - 비밀번호 빈값이면 400 반환")
    void login_blankPassword() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "");

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
