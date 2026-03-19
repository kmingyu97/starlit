package com.starlit.communityservice.controller;

import com.starlit.communityservice.dto.LikeResponse;
import com.starlit.communityservice.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 좋아요 컨트롤러.
 *
 * <pre>
 * POST /api/community/posts/{id}/like → 좋아요 토글 (회원)
 * </pre>
 */
@RestController
@RequestMapping("/api/community")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /** 좋아요를 토글한다 (좋아요 ↔ 취소). */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(likeService.toggleLike(postId, userId));
    }
}
