package com.starlit.communityservice.service;

import com.starlit.communityservice.dto.LikeResponse;
import com.starlit.communityservice.entity.Post;
import com.starlit.communityservice.entity.PostLike;
import com.starlit.communityservice.repository.PostLikeRepository;
import com.starlit.communityservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("좋아요가 없으면 추가한다")
    void toggleLike_add() {
        Post post = Post.builder().userId(1L).nickname("작성자").category("자유")
                .title("제목").content("내용").build();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostIdAndUserId(1L, 10L)).willReturn(Optional.empty());

        LikeResponse response = likeService.toggleLike(1L, 10L);

        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);
        verify(postLikeRepository).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요가 있으면 취소한다")
    void toggleLike_remove() {
        Post post = Post.builder().userId(1L).nickname("작성자").category("자유")
                .title("제목").content("내용").build();
        post.incrementLikeCount();
        PostLike like = new PostLike(1L, 10L);

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostIdAndUserId(1L, 10L)).willReturn(Optional.of(like));

        LikeResponse response = likeService.toggleLike(1L, 10L);

        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isZero();
        verify(postLikeRepository).delete(like);
    }
}
