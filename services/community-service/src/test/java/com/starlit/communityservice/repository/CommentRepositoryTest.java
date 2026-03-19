package com.starlit.communityservice.repository;

import com.starlit.communityservice.entity.Comment;
import com.starlit.communityservice.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글의 댓글을 생성일 오름차순으로 조회한다")
    void findByPostIdOrderByCreatedAtAsc() {
        Post post = postRepository.save(Post.builder()
                .userId(1L).nickname("작성자").category("자유")
                .title("제목").content("내용").build());

        commentRepository.save(Comment.builder()
                .postId(post.getId()).userId(2L).nickname("댓글러1").content("첫 댓글").build());
        commentRepository.save(Comment.builder()
                .postId(post.getId()).userId(3L).nickname("댓글러2").content("두번째 댓글").build());

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getContent()).isEqualTo("첫 댓글");
    }
}
