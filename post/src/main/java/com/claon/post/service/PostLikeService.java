package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.exception.BadRequestException;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.NotFoundException;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.common.validator.IsPrivateValidator;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostLike;
import com.claon.post.dto.LikeFindResponseDto;
import com.claon.post.dto.LikeResponseDto;
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
    private final PaginationFactory paginationFactory;

    @Transactional
    public LikeResponseDto createLike(String userId, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        postLikeRepository.findByLikerIdAndPost(userId, post).ifPresent(
                like -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 좋아요 했습니다."
                    );
                }
        );

        return LikeResponseDto.from(
                postLikeRepository.save(PostLike.of(userId, post)),
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public LikeResponseDto deleteLike(String userId, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        PostLike like = postLikeRepository.findByLikerIdAndPost(userId, post).orElseThrow(
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
    public Pagination<LikeFindResponseDto> findLikeByPost(
            String userId,
            String postId,
            Pageable pageable
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriterId().equals(userId)) {
//            IsPrivateValidator.of(post.getWriter().getNickname(), post.getWriter().getIsPrivate()).validate();
//
//            if (!blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).isEmpty()) {
//                throw new UnauthorizedException(
//                        ErrorCode.NOT_ACCESSIBLE,
//                        String.format("%s을 찾을 수 없습니다.", post.getWriter().getNickname())
//                );
//            }
        }

        return this.paginationFactory.create(
                postLikeRepositorySupport.findAllByPost(post.getId(), userId, pageable)
                        .map(LikeFindResponseDto::from)
        );
    }
}