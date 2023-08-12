package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.NotFoundException;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.common.validator.IdEqualValidator;
import com.claon.post.common.validator.IsPrivateValidator;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostComment;
import com.claon.post.dto.*;
import com.claon.post.repository.PostCommentRepository;
import com.claon.post.repository.PostCommentRepositorySupport;
import com.claon.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostCommentRepositorySupport postCommentRepositorySupport;
    private final PostRepository postRepository;
    private final PaginationFactory paginationFactory;

    @Transactional
    public CommentResponseDto createComment(
            String userId,
            String postId,
            CommentCreateRequestDto commentCreateRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriterId().equals(userId)) {
//            IsPrivateValidator.of(post.getWriterId(), post.getWriter().getIsPrivate()).validate();

//            if (!blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).isEmpty()) {
//                throw new UnauthorizedException(
//                        ErrorCode.NOT_ACCESSIBLE,
//                        String.format("%s을 찾을 수 없습니다.", post.getWriter().getNickname())
//                );
//            }
        }

        return CommentResponseDto.from(
                postCommentRepository.save(
                        PostComment.of(
                                commentCreateRequestDto.getContent(),
                                userId,
                                post,
                                Optional.ofNullable(commentCreateRequestDto.getParentCommentId())
                                        .map(parentCommentId ->
                                                postCommentRepository.findById(commentCreateRequestDto.getParentCommentId())
                                                        .orElseThrow(
                                                                () -> new NotFoundException(
                                                                        ErrorCode.DATA_DOES_NOT_EXIST,
                                                                        "상위 댓글을 찾을 수 없습니다."
                                                                )))
                                        .orElse(null)
                        )
                ));
    }

    @Transactional(readOnly = true)
    public Pagination<CommentFindResponseDto> findCommentsByPost(
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
                postCommentRepositorySupport.findParentCommentByPost(post.getId(), userId, pageable)
        );
    }

    @Transactional(readOnly = true)
    public Pagination<ChildCommentResponseDto> findAllChildCommentsByParent(
            String userId,
            String parentId,
            Pageable pageable
    ) {
        PostComment postComment = postCommentRepository.findByIdAndIsDeletedFalse(parentId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
                )
        );

        if (!postComment.getPost().getWriterId().equals(userId)) {
//            IsPrivateValidator.of(postComment.getPost().getWriter().getNickname(), postComment.getPost().getWriter().getIsPrivate()).validate();
//
//            if (!blockUserRepository.findBlock(user.getId(), postComment.getPost().getWriter().getId()).isEmpty()) {
//                throw new UnauthorizedException(
//                        ErrorCode.NOT_ACCESSIBLE,
//                        String.format("%s을 찾을 수 없습니다.", postComment.getPost().getWriter().getNickname())
//                );
//            }
        }

        return this.paginationFactory.create(
                postCommentRepositorySupport.findChildCommentByParentComment(postComment.getId(), userId, pageable)
                        .map(childComment -> ChildCommentResponseDto.from(childComment, userId))
        );
    }

    @Transactional
    public CommentResponseDto deleteComment(String userId, String commentId) {
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(postComment.getWriterId(), userId).validate();

        postComment.delete();

        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }

    @Transactional
    public CommentResponseDto updateComment(
            String userId,
            String commentId,
            CommentUpdateRequestDto commentUpdateRequestDto
    ) {
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(postComment.getWriterId(), userId).validate();

        postComment.updateContent(commentUpdateRequestDto.getContent());

        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }
}