package com.starlit.communityservice.service;

import com.starlit.communityservice.common.exception.CustomException;
import com.starlit.communityservice.common.exception.ErrorCode;
import com.starlit.communityservice.dto.CommentRequest;
import com.starlit.communityservice.dto.CommentResponse;
import com.starlit.communityservice.entity.Comment;
import com.starlit.communityservice.entity.Post;
import com.starlit.communityservice.repository.CommentRepository;
import com.starlit.communityservice.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 서비스.
 *
 * <p>댓글 조회, 작성, 삭제와 게시글의 댓글 수 동기화를 담당한다.</p>
 */
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    /**
     * 게시글의 댓글 목록을 조회한다.
     *
     * @param postId 게시글 ID
     * @return 댓글 목록 (생성일 오름차순)
     */
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 댓글을 작성한다.
     *
     * @param postId   게시글 ID
     * @param userId   작성자 ID
     * @param nickname 작성자 닉네임
     * @param request  댓글 요청 DTO
     * @return 작성된 댓글
     */
    @Transactional
    public CommentResponse createComment(Long postId, Long userId, String nickname, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .nickname(nickname)
                .content(request.content())
                .build();

        post.incrementCommentCount();
        return toResponse(commentRepository.save(comment));
    }

    /**
     * 댓글을 삭제한다. 본인만 삭제할 수 있다.
     *
     * @param commentId 댓글 ID
     * @param userId    요청자 ID
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        postRepository.findById(comment.getPostId())
                .ifPresent(Post::decrementCommentCount);

        commentRepository.delete(comment);
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(), comment.getUserId(), comment.getNickname(),
                comment.getContent(), comment.getCreatedAt());
    }
}
