package com.starlit.userservice.repository;

import com.starlit.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository 통합 테스트.
 *
 * H2 인메모리 DB를 사용하여 JPA 쿼리 메서드가 올바르게 동작하는지 검증한다.
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .email("test@example.com")
                .password("hashedPassword")
                .nickname("tester")
                .build();
        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("이메일로 사용자를 조회할 수 있다")
    void findByEmail() {
        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("존재하지 않는 이메일 조회 시 빈 Optional을 반환한다")
    void findByEmail_notFound() {
        // when
        Optional<User> found = userRepository.findByEmail("nobody@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("이메일 중복 여부를 확인할 수 있다")
    void existsByEmail() {
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
    }

    @Test
    @DisplayName("닉네임 중복 여부를 확인할 수 있다")
    void existsByNickname() {
        assertThat(userRepository.existsByNickname("tester")).isTrue();
        assertThat(userRepository.existsByNickname("otherNick")).isFalse();
    }

    @Test
    @DisplayName("저장된 사용자의 createdAt이 자동으로 설정된다")
    void createdAtIsAutoSet() {
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }
}
