package com.starlit.communityservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 댓글 엔티티.
 *
 * <p>게시글에 달리는 댓글을 저장한다. 게시글 삭제 시 CASCADE로 함께 삭제된다.</p>
 */
@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게시글 ID */
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /** 작성자 ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 작성자 닉네임 (비정규화) */
    @Column(nullable = false, length = 30)
    private String nickname;

    /** 댓글 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Comment(Long postId, Long userId, String nickname, String content) {
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
