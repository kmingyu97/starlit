package com.starlit.communityservice.service;

import com.starlit.communityservice.common.exception.CustomException;
import com.starlit.communityservice.common.exception.ErrorCode;
import com.starlit.communityservice.dto.LikeResponse;
import com.starlit.communityservice.entity.Post;
import com.starlit.communityservice.entity.PostLike;
import com.starlit.communityservice.repository.PostLikeRepository;
import com.starlit.communityservice.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 좋아요 서비스.
 *
 * <p>게시글 좋아요 토글(추가/취소)과 게시글의 좋아요 수 동기화를 담당한다.</p>
 */
@Service
@Transactional
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    public LikeService(PostLikeRepository postLikeRepository, PostRepository postRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
    }

    /**
     * 좋아요를 토글한다. 이미 좋아요한 상태면 취소, 아니면 추가한다.
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     * @return 좋아요 상태와 총 좋아요 수
     */
    public LikeResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return postLikeRepository.findByPostIdAndUserId(postId, userId)
                .map(like -> {
                    postLikeRepository.delete(like);
                    post.decrementLikeCount();
                    return new LikeResponse(false, post.getLikeCount());
                })
                .orElseGet(() -> {
                    postLikeRepository.save(new PostLike(postId, userId));
                    post.incrementLikeCount();
                    return new LikeResponse(true, post.getLikeCount());
                });
    }
}
