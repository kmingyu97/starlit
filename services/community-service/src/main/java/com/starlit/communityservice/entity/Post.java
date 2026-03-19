package com.starlit.communityservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 엔티티.
 *
 * <p>카테고리별 게시글을 저장한다. 조회수·좋아요수·댓글수는 비정규화하여 성능을 확보한다.</p>
 */
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 작성자 ID (Gateway에서 X-User-Id로 전달) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 작성자 닉네임 (비정규화) */
    @Column(nullable = false, length = 30)
    private String nickname;

    /** 카테고리 (자유/종목토론/뉴스/질문) */
    @Column(nullable = false, length = 30)
    private String category;

    /** 제목 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 본문 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 조회수 */
    @Column(name = "view_count")
    private int viewCount;

    /** 좋아요 수 */
    @Column(name = "like_count")
    private int likeCount;

    /** 댓글 수 */
    @Column(name = "comment_count")
    private int commentCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Post(Long userId, String nickname, String category, String title, String content) {
        this.userId = userId;
        this.nickname = nickname;
        this.category = category;
        this.title = title;
        this.content = content;
        this.viewCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** 게시글을 수정한다. */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    /** 조회수를 1 증가시킨다. */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /** 좋아요 수를 1 증가시킨다. */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /** 좋아요 수를 1 감소시킨다. */
    public void decrementLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }

    /** 댓글 수를 1 증가시킨다. */
    public void incrementCommentCount() {
        this.commentCount++;
    }

    /** 댓글 수를 1 감소시킨다. */
    public void decrementCommentCount() {
        if (this.commentCount > 0) this.commentCount--;
    }
}
