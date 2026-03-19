package com.starlit.communityservice.service;

import com.starlit.communityservice.common.exception.CustomException;
import com.starlit.communityservice.dto.PostRequest;
import com.starlit.communityservice.dto.PostResponse;
import com.starlit.communityservice.entity.Post;
import com.starlit.communityservice.repository.CommentRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    private Post createTestPost() {
        return Post.builder()
                .userId(1L).nickname("테스터").category("자유")
                .title("테스트 제목").content("테스트 내용").build();
    }

    @Test
    @DisplayName("게시글을 작성한다")
    void createPost() {
        Post post = createTestPost();
        given(postRepository.save(any(Post.class))).willReturn(post);

        PostResponse response = postService.createPost(1L, "테스터",
                new PostRequest("자유", "테스트 제목", "테스트 내용"));

        assertThat(response.title()).isEqualTo("테스트 제목");
        assertThat(response.nickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("게시글 상세 조회 시 조회수가 증가한다")
    void getPost_incrementsViewCount() {
        Post post = createTestPost();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        PostResponse response = postService.getPost(1L);

        assertThat(response.viewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 예외가 발생한다")
    void getPost_notFound() {
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(999L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("본인이 작성한 게시글을 수정한다")
    void updatePost() {
        Post post = createTestPost();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        PostResponse response = postService.updatePost(1L, 1L,
                new PostRequest("자유", "수정 제목", "수정 내용"));

        assertThat(response.title()).isEqualTo("수정 제목");
    }

    @Test
    @DisplayName("타인의 게시글 수정 시 403 예외가 발생한다")
    void updatePost_forbidden() {
        Post post = createTestPost();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.updatePost(1L, 99L,
                new PostRequest("자유", "수정", "내용")))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("게시글을 삭제하면 댓글과 좋아요도 함께 삭제된다")
    void deletePost() {
        Post post = createTestPost();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        postService.deletePost(1L, 1L);

        verify(commentRepository).deleteByPostId(1L);
        verify(postLikeRepository).deleteByPostId(1L);
        verify(postRepository).delete(post);
    }
}
