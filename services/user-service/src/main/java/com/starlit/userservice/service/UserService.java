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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 관련 비즈니스 로직.
 *
 * <p>회원가입, 로그인(JWT 발급), 프로필 조회/수정을 처리한다.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입을 처리한다.
     *
     * <p>이메일·닉네임 중복을 검사하고, 비밀번호를 BCrypt로 해싱하여 저장한다.</p>
     *
     * @param request 회원가입 요청 (이메일, 비밀번호, 닉네임)
     * @return 생성된 사용자 정보
     * @throws CustomException 이메일(DUPLICATE_EMAIL) 또는 닉네임(DUPLICATE_NICKNAME) 중복 시
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        User saved = userRepository.save(user);

        return new SignupResponse(saved.getId(), saved.getEmail(), saved.getNickname());
    }

    /**
     * 로그인을 처리하고 JWT 토큰을 발급한다.
     *
     * <p>이메일로 사용자를 조회한 뒤 비밀번호를 BCrypt로 비교하고,
     * 일치하면 JWT 토큰을 생성하여 반환한다.</p>
     *
     * @param request 로그인 요청 (이메일, 비밀번호)
     * @return JWT 토큰과 닉네임
     * @throws CustomException 이메일 미존재 또는 비밀번호 불일치 시 (INVALID_CREDENTIALS)
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtProvider.createToken(user.getId(), user.getEmail());
        return new LoginResponse(token, user.getNickname());
    }

    /**
     * 사용자 프로필을 조회한다.
     *
     * @param userId Gateway가 JWT에서 추출한 사용자 ID
     * @return 프로필 정보 (id, email, nickname, createdAt)
     * @throws CustomException 사용자가 존재하지 않으면 NOT_FOUND
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return new ProfileResponse(user.getId(), user.getEmail(), user.getNickname(), user.getCreatedAt());
    }

    /**
     * 사용자 프로필(닉네임)을 수정한다.
     *
     * <p>기존과 동일한 닉네임이면 중복 체크 없이 통과한다.</p>
     *
     * @param userId  Gateway가 JWT에서 추출한 사용자 ID
     * @param request 수정 요청 (닉네임)
     * @return 수정된 프로필 정보
     * @throws CustomException 사용자 미존재(NOT_FOUND) 또는 닉네임 중복(DUPLICATE_NICKNAME)
     */
    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!user.getNickname().equals(request.nickname())
                && userRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        user.updateNickname(request.nickname());

        return new ProfileResponse(user.getId(), user.getEmail(), user.getNickname(), user.getCreatedAt());
    }
}
