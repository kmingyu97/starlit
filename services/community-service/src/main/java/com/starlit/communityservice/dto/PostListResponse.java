package com.starlit.communityservice.dto;

import java.time.LocalDateTime;

/**
 * 게시글 목록 응답 DTO (본문 제외).
 *
 * @param id           게시글 ID
 * @param nickname     작성자 닉네임
 * @param category     카테고리
 * @param title        제목
 * @param viewCount    조회수
 * @param likeCount    좋아요 수
 * @param commentCount 댓글 수
 * @param createdAt    작성일
 */
public record PostListResponse(
        Long id,
        String nickname,
        String category,
        String title,
        int viewCount,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt
) {
}
