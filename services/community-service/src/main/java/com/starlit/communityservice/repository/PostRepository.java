package com.starlit.communityservice.repository;

import com.starlit.communityservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 게시글 Repository.
 *
 * <p>카테고리별 필터와 페이징을 지원한다.</p>
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /** 카테고리별 게시글 목록을 조회한다. */
    Page<Post> findByCategory(String category, Pageable pageable);
}
