package com.starlit.communityservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 게시글 작성/수정 요청 DTO.
 *
 * @param category 카테고리 (자유/종목토론/뉴스/질문)
 * @param title    제목 (1~200자)
 * @param content  본문
 */
public record PostRequest(
        @NotBlank(message = "카테고리를 선택해주세요.")
        String category,

        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {
}
