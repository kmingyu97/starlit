package com.starlit.communityservice.dto;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO.
 *
 * @param id           게시글 ID
 * @param userId       작성자 ID
 * @param nickname     작성자 닉네임
 * @param category     카테고리
 * @param title        제목
 * @param content      본문
 * @param viewCount    조회수
 * @param likeCount    좋아요 수
 * @param commentCount 댓글 수
 * @param createdAt    작성일
 * @param updatedAt    수정일
 */
public record PostResponse(
        Long id,
        Long userId,
        String nickname,
        String category,
        String title,
        String content,
        int viewCount,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
