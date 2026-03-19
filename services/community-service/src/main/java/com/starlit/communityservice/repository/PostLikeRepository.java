package com.starlit.communityservice.repository;

import com.starlit.communityservice.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 게시글 좋아요 Repository.
 *
 * <p>좋아요 토글(추가/취소)과 존재 여부 확인을 지원한다.</p>
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /** 특정 게시글에 대한 사용자의 좋아요를 조회한다. */
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    /** 특정 게시글에 대한 사용자의 좋아요 여부를 확인한다. */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /** 게시글의 모든 좋아요를 삭제한다. */
    void deleteByPostId(Long postId);
}
