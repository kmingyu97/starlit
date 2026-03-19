package com.starlit.communityservice.repository;

import com.starlit.communityservice.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글을 저장하고 조회한다")
    void saveAndFind() {
        Post post = Post.builder()
                .userId(1L).nickname("테스터").category("자유")
                .title("테스트 제목").content("테스트 내용").build();

        Post saved = postRepository.save(post);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("테스트 제목");
        assertThat(saved.getViewCount()).isZero();
    }

    @Test
    @DisplayName("카테고리별 게시글을 조회한다")
    void findByCategory() {
        postRepository.save(Post.builder()
                .userId(1L).nickname("유저1").category("자유")
                .title("자유1").content("내용").build());
        postRepository.save(Post.builder()
                .userId(1L).nickname("유저1").category("자유")
                .title("자유2").content("내용").build());
        postRepository.save(Post.builder()
                .userId(1L).nickname("유저1").category("질문")
                .title("질문1").content("내용").build());

        Page<Post> result = postRepository.findByCategory("자유",
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("게시글을 수정한다")
    void update() {
        Post post = postRepository.save(Post.builder()
                .userId(1L).nickname("유저1").category("자유")
                .title("원래 제목").content("원래 내용").build());

        post.update("수정 제목", "수정 내용");

        assertThat(post.getTitle()).isEqualTo("수정 제목");
        assertThat(post.getContent()).isEqualTo("수정 내용");
    }
}
