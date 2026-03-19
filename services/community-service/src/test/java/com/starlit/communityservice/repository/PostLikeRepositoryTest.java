package com.starlit.communityservice.repository;

import com.starlit.communityservice.entity.PostLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Test
    @DisplayName("좋아요를 저장하고 조회한다")
    void saveAndFind() {
        postLikeRepository.save(new PostLike(1L, 10L));

        Optional<PostLike> result = postLikeRepository.findByPostIdAndUserId(1L, 10L);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("좋아요 존재 여부를 확인한다")
    void existsByPostIdAndUserId() {
        postLikeRepository.save(new PostLike(1L, 10L));

        assertThat(postLikeRepository.existsByPostIdAndUserId(1L, 10L)).isTrue();
        assertThat(postLikeRepository.existsByPostIdAndUserId(1L, 99L)).isFalse();
    }
}
