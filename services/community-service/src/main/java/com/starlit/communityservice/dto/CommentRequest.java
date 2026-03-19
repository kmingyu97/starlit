package com.starlit.communityservice.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 댓글 작성 요청 DTO.
 *
 * @param content 댓글 내용
 */
public record CommentRequest(
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        String content
) {
}
