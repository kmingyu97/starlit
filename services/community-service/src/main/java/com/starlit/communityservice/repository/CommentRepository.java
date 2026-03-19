package com.starlit.communityservice.repository;

import com.starlit.communityservice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 댓글 Repository.
 *
 * <p>게시글별 댓글 조회와 삭제를 지원한다.</p>
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** 게시글의 댓글 목록을 생성일 오름차순으로 조회한다. */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    /** 게시글의 모든 댓글을 삭제한다. */
    void deleteByPostId(Long postId);
}
