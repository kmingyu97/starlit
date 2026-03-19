package com.starlit.communityservice.controller;

import com.starlit.communityservice.dto.PostListResponse;
import com.starlit.communityservice.dto.PostRequest;
import com.starlit.communityservice.dto.PostResponse;
import com.starlit.communityservice.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 컨트롤러.
 *
 * <p>목록/상세 조회는 공개 API이고, 작성/수정/삭제는 인증(X-User-Id)이 필요하다.</p>
 *
 * <pre>
 * GET    /api/community/posts      → 목록 (공개)
 * GET    /api/community/posts/{id} → 상세 (공개)
 * POST   /api/community/posts      → 작성 (회원)
 * PUT    /api/community/posts/{id} → 수정 (본인)
 * DELETE /api/community/posts/{id} → 삭제 (본인)
 * </pre>
 */
@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** 게시글 목록을 조회한다 (카테고리 필터, 페이징). */
    @GetMapping
    public ResponseEntity<Page<PostListResponse>> getPosts(
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(category, pageable));
    }

    /** 게시글 상세를 조회한다. */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    /** 게시글을 작성한다. */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestParam(defaultValue = "익명") String nickname,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(userId, nickname, request));
    }

    /** 게시글을 수정한다 (본인만). */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.updatePost(id, userId, request));
    }

    /** 게시글을 삭제한다 (본인만). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }
}
