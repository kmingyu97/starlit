package com.starlit.communityservice.service;

import com.starlit.communityservice.common.exception.CustomException;
import com.starlit.communityservice.common.exception.ErrorCode;
import com.starlit.communityservice.dto.PostListResponse;
import com.starlit.communityservice.dto.PostRequest;
import com.starlit.communityservice.dto.PostResponse;
import com.starlit.communityservice.entity.Post;
import com.starlit.communityservice.repository.CommentRepository;
import com.starlit.communityservice.repository.PostLikeRepository;
import com.starlit.communityservice.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 서비스.
 *
 * <p>게시글 CRUD와 조회수 증가 등의 비즈니스 로직을 담당한다.</p>
 */
@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    public PostService(PostRepository postRepository,
                       CommentRepository commentRepository,
                       PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
    }

    /**
     * 게시글 목록을 조회한다.
     *
     * @param category 카테고리 필터 (nullable)
     * @param pageable 페이징 정보
     * @return 게시글 목록 (본문 제외)
     */
    public Page<PostListResponse> getPosts(String category, Pageable pageable) {
        Page<Post> posts = (category != null && !category.isBlank())
                ? postRepository.findByCategory(category, pageable)
                : postRepository.findAll(pageable);

        return posts.map(this::toListResponse);
    }

    /**
     * 게시글 상세를 조회한다. 조회수가 1 증가한다.
     *
     * @param postId 게시글 ID
     * @return 게시글 상세 (본문 포함)
     */
    @Transactional
    public PostResponse getPost(Long postId) {
        Post post = findPostOrThrow(postId);
        post.incrementViewCount();
        return toResponse(post);
    }

    /**
     * 게시글을 작성한다.
     *
     * @param userId   작성자 ID (X-User-Id)
     * @param nickname 작성자 닉네임 (X-User-Email 대신 프론트에서 전달)
     * @param request  게시글 요청 DTO
     * @return 작성된 게시글
     */
    @Transactional
    public PostResponse createPost(Long userId, String nickname, PostRequest request) {
        Post post = Post.builder()
                .userId(userId)
                .nickname(nickname)
                .category(request.category())
                .title(request.title())
                .content(request.content())
                .build();

        return toResponse(postRepository.save(post));
    }

    /**
     * 게시글을 수정한다. 본인만 수정할 수 있다.
     *
     * @param postId  게시글 ID
     * @param userId  요청자 ID
     * @param request 수정 요청 DTO
     * @return 수정된 게시글
     */
    @Transactional
    public PostResponse updatePost(Long postId, Long userId, PostRequest request) {
        Post post = findPostOrThrow(postId);
        validateOwner(post, userId);

        post.update(request.title(), request.content());
        return toResponse(post);
    }

    /**
     * 게시글을 삭제한다. 본인만 삭제할 수 있다. 댓글·좋아요도 함께 삭제된다.
     *
     * @param postId 게시글 ID
     * @param userId 요청자 ID
     */
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = findPostOrThrow(postId);
        validateOwner(post, userId);

        commentRepository.deleteByPostId(postId);
        postLikeRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void validateOwner(Post post, Long userId) {
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    private PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getId(), post.getUserId(), post.getNickname(),
                post.getCategory(), post.getTitle(), post.getContent(),
                post.getViewCount(), post.getLikeCount(), post.getCommentCount(),
                post.getCreatedAt(), post.getUpdatedAt());
    }

    private PostListResponse toListResponse(Post post) {
        return new PostListResponse(
                post.getId(), post.getNickname(), post.getCategory(),
                post.getTitle(), post.getViewCount(), post.getLikeCount(),
                post.getCommentCount(), post.getCreatedAt());
    }
}
