package com.starlit.communityservice.controller;

import com.starlit.communityservice.dto.CommentRequest;
import com.starlit.communityservice.dto.CommentResponse;
import com.starlit.communityservice.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 컨트롤러.
 *
 * <pre>
 * GET    /api/community/posts/{id}/comments → 댓글 목록 (공개)
 * POST   /api/community/posts/{id}/comments → 댓글 작성 (회원)
 * DELETE /api/community/comments/{id}       → 댓글 삭제 (본인)
 * </pre>
 */
@RestController
@RequestMapping("/api/community")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 게시글의 댓글 목록을 조회한다. */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    /** 댓글을 작성한다. */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "익명") String nickname,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, userId, nickname, request));
    }

    /** 댓글을 삭제한다 (본인만). */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }
}
