package com.starlit.communityservice.dto;

/**
 * 좋아요 토글 응답 DTO.
 *
 * @param liked     현재 좋아요 상태 (true=좋아요, false=취소)
 * @param likeCount 게시글의 총 좋아요 수
 */
public record LikeResponse(
        boolean liked,
        int likeCount
) {
}
