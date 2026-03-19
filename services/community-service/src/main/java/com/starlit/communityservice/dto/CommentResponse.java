package com.starlit.communityservice.dto;

import java.time.LocalDateTime;

/**
 * 댓글 응답 DTO.
 *
 * @param id        댓글 ID
 * @param userId    작성자 ID
 * @param nickname  작성자 닉네임
 * @param content   댓글 내용
 * @param createdAt 작성일
 */
public record CommentResponse(
        Long id,
        Long userId,
        String nickname,
        String content,
        LocalDateTime createdAt
) {
}
