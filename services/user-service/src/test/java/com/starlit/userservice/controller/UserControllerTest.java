package com.starlit.userservice.controller;

import tools.jackson.databind.ObjectMapper;
import com.starlit.userservice.common.exception.CustomException;
import com.starlit.userservice.common.exception.ErrorCode;
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
}
