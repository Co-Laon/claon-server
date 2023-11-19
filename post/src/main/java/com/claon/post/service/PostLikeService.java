package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.common.exception.BadRequestException;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.NotFoundException;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostLike;
import com.claon.post.dto.LikerResponseDto;
import com.claon.post.dto.LikeResponseDto;
import com.claon.post.repository.BlockUserRepository;
import com.claon.post.repository.PostLikeRepository;
import com.claon.post.repository.PostLikeRepositorySupport;
import com.claon.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeRepositorySupport postLikeRepositorySupport;
    private final BlockUserRepository blockUserRepository;
    private final PaginationFactory paginationFactory;

    @Transactional
    public LikeResponseDto createLike(RequestUserInfo userInfo, String postId) {
        Post post = findPostById(postId);

        postLikeRepository.findByLikerIdAndPost(userInfo.id(), post).ifPresent(
                like -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 좋아요 했습니다."
                    );
                }
        );

        return LikeResponseDto.from(
                postLikeRepository.save(PostLike.of(userInfo.id(), post)),
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public LikeResponseDto deleteLike(RequestUserInfo userInfo, String postId) {
        Post post = findPostById(postId);

        PostLike like = postLikeRepository.findByLikerIdAndPost(userInfo.id(), post).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "아직 좋아요 하지 않았습니다."
                )
        );

        postLikeRepository.delete(like);

        return LikeResponseDto.from(
                like,
                postLikeRepository.countByPost(like.getPost())
        );
    }

    @Transactional(readOnly = true)
    public Pagination<LikerResponseDto> findLikeByPost(
            RequestUserInfo userInfo,
            String postId,
            Pageable pageable
    ) {
        Post post = findPostById(postId);

        validateBlockedUser(userInfo, post.getWriterId());

        return this.paginationFactory.create(
                postLikeRepositorySupport.findAllByPost(post.getId(), userInfo.id(), pageable)
                        .map(LikerResponseDto::from)
        );
    }

    private Post findPostById(String postId) {
        return postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );
    }

    private void validateBlockedUser(RequestUserInfo userInfo, String writerId) {
        if (!userInfo.id().equals(writerId)) {
            if (!blockUserRepository.findBlock(userInfo.id(), writerId).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", writerId)
                );
            }
        }
    }
}
