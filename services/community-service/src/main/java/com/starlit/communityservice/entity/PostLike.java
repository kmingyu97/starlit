package com.starlit.communityservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 좋아요 엔티티.
 *
 * <p>게시글과 사용자의 좋아요 관계를 저장한다. (post_id, user_id) 조합은 유니크이다.</p>
 */
@Entity
@Table(name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게시글 ID */
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /** 사용자 ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    public PostLike(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}
