package com.starlit.communityservice.service;

import com.starlit.communityservice.common.exception.CustomException;
import com.starlit.communityservice.dto.CommentRequest;
import com.starlit.communityservice.dto.CommentResponse;
import com.starlit.communityservice.entity.Comment;
import com.starlit.communityservice.entity.Post;
import com.starlit.communityservice.repository.CommentRepository;
import com.starlit.communityservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    @DisplayName("댓글을 작성하면 게시글 댓글 수가 증가한다")
    void createComment() {
        Post post = Post.builder().userId(1L).nickname("작성자").category("자유")
                .title("제목").content("내용").build();
        Comment comment = Comment.builder().postId(1L).userId(2L).nickname("댓글러")
                .content("좋은 글이네요").build();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        CommentResponse response = commentService.createComment(1L, 2L, "댓글러",
                new CommentRequest("좋은 글이네요"));

        assertThat(response.content()).isEqualTo("좋은 글이네요");
        assertThat(post.getCommentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 목록을 조회한다")
    void getComments() {
        Comment c1 = Comment.builder().postId(1L).userId(2L).nickname("A").content("댓글1").build();
        Comment c2 = Comment.builder().postId(1L).userId(3L).nickname("B").content("댓글2").build();
        given(commentRepository.findByPostIdOrderByCreatedAtAsc(1L)).willReturn(List.of(c1, c2));

        List<CommentResponse> result = commentService.getComments(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("본인 댓글을 삭제하면 게시글 댓글 수가 감소한다")
    void deleteComment() {
        Post post = Post.builder().userId(1L).nickname("작성자").category("자유")
                .title("제목").content("내용").build();
        post.incrementCommentCount();
        Comment comment = Comment.builder().postId(1L).userId(2L).nickname("댓글러")
                .content("댓글").build();

        given(commentRepository.findById(10L)).willReturn(Optional.of(comment));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        commentService.deleteComment(10L, 2L);

        verify(commentRepository).delete(comment);
        assertThat(post.getCommentCount()).isZero();
    }

    @Test
    @DisplayName("타인의 댓글 삭제 시 403 예외가 발생한다")
    void deleteComment_forbidden() {
        Comment comment = Comment.builder().postId(1L).userId(2L).nickname("댓글러")
                .content("댓글").build();
        given(commentRepository.findById(10L)).willReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(10L, 99L))
                .isInstanceOf(CustomException.class);
    }
}
