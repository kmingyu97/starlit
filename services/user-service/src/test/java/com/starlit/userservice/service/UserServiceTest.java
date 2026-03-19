package com.starlit.userservice.service;

import com.starlit.userservice.common.exception.CustomException;
import com.starlit.userservice.common.exception.ErrorCode;
import com.starlit.userservice.config.JwtProvider;
import com.starlit.userservice.dto.LoginRequest;
import com.starlit.userservice.dto.LoginResponse;
import com.starlit.userservice.dto.ProfileResponse;
import com.starlit.userservice.dto.ProfileUpdateRequest;
import com.starlit.userservice.dto.SignupRequest;
import com.starlit.userservice.dto.SignupResponse;
import com.starlit.userservice.entity.User;
import com.starlit.userservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("회원가입 성공 - 유저가 저장되고 응답이 반환된다")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "tester");

        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.existsByNickname("tester")).willReturn(false);
        given(passwordEncoder.encode("Password1!")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return User.builder()
                    .id(1L)
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .nickname(user.getNickname())
                    .build();
        });

        // when
        SignupResponse response = userService.signup(request);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.nickname()).isEqualTo("tester");

        verify(passwordEncoder).encode("Password1!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복 시 DUPLICATE_EMAIL 예외")
    void signup_duplicateEmail() {
        // given
        SignupRequest request = new SignupRequest("dup@example.com", "Password1!", "tester");
        given(userRepository.existsByEmail("dup@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
                });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복 시 DUPLICATE_NICKNAME 예외")
    void signup_duplicateNickname() {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "dupNick");
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.existsByNickname("dupNick")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
                });

        verify(userRepository, never()).save(any());
    }

    // === 로그인 테스트 ===

    @Test
    @DisplayName("로그인 성공 - JWT 토큰과 닉네임이 반환된다")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "Password1!");
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("tester")
                .build();

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("Password1!", "encodedPassword")).willReturn(true);
        given(jwtProvider.createToken(1L, "test@example.com")).willReturn("jwt-token");

        // when
        LoginResponse response = userService.login(request);

        // then
        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.nickname()).isEqualTo("tester");
        verify(jwtProvider).createToken(1L, "test@example.com");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일이면 INVALID_CREDENTIALS 예외")
    void login_emailNotFound() {
        // given
        LoginRequest request = new LoginRequest("nouser@example.com", "Password1!");
        given(userRepository.findByEmail("nouser@example.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
                });
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치 시 INVALID_CREDENTIALS 예외")
    void login_wrongPassword() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "WrongPass1!");
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("tester")
                .build();

        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("WrongPass1!", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
                });

        verify(jwtProvider, never()).createToken(any(), any());
    }

    // === 프로필 테스트 ===

    @Test
    @DisplayName("프로필 조회 성공 - 유저 정보가 반환된다")
    void getProfile_success() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("tester")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        ProfileResponse response = userService.getProfile(1L);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.nickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("프로필 조회 실패 - 존재하지 않는 유저면 NOT_FOUND 예외")
    void getProfile_notFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getProfile(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
                });
    }

    @Test
    @DisplayName("프로필 수정 성공 - 닉네임이 변경된다")
    void updateProfile_success() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("oldNick")
                .build();
        ProfileUpdateRequest request = new ProfileUpdateRequest("newNick");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname("newNick")).willReturn(false);

        // when
        ProfileResponse response = userService.updateProfile(1L, request);

        // then
        assertThat(response.nickname()).isEqualTo("newNick");
    }

    @Test
    @DisplayName("프로필 수정 실패 - 닉네임 중복 시 DUPLICATE_NICKNAME 예외")
    void updateProfile_duplicateNickname() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("oldNick")
                .build();
        ProfileUpdateRequest request = new ProfileUpdateRequest("takenNick");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname("takenNick")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(1L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> {
                    CustomException ce = (CustomException) ex;
                    assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
                });
    }

    @Test
    @DisplayName("프로필 수정 - 기존과 같은 닉네임이면 중복 체크 없이 성공한다")
    void updateProfile_sameNickname_success() {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("sameNick")
                .build();
        ProfileUpdateRequest request = new ProfileUpdateRequest("sameNick");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        ProfileResponse response = userService.updateProfile(1L, request);

        // then
        assertThat(response.nickname()).isEqualTo("sameNick");
        verify(userRepository, never()).existsByNickname(any());
    }

    // === 회원가입 기타 테스트 ===

    @Test
    @DisplayName("회원가입 시 비밀번호가 BCrypt로 해싱되어 저장된다")
    void signup_passwordIsEncoded() {
        // given
        SignupRequest request = new SignupRequest("test@example.com", "Password1!", "tester");

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByNickname(anyString())).willReturn(false);
        given(passwordEncoder.encode("Password1!")).willReturn("$2a$10$hashedValue");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return User.builder()
                    .id(1L)
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .nickname(user.getNickname())
                    .build();
        });

        // when
        userService.signup(request);

        // then
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password1!");
    }
}
