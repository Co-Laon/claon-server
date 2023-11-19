package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.NotFoundException;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.common.validator.IdEqualValidator;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostComment;
import com.claon.post.dto.*;
import com.claon.post.dto.request.CommentCreateRequestDto;
import com.claon.post.dto.request.CommentUpdateRequestDto;
import com.claon.post.repository.BlockUserRepository;
import com.claon.post.repository.PostCommentRepository;
import com.claon.post.repository.PostCommentRepositorySupport;
import com.claon.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostCommentRepositorySupport postCommentRepositorySupport;
    private final PostRepository postRepository;
    private final BlockUserRepository blockUserRepository;

    private final PaginationFactory paginationFactory;

    @Transactional
    public CommentResponseDto createComment(
            RequestUserInfo userInfo,
            String postId,
            CommentCreateRequestDto commentCreateRequestDto
    ) {
        Post post = findPostById(postId);

        validateBlockedUser(userInfo, post.getWriterId());

        Function<String, PostComment> findParentComment = commentId -> postCommentRepository.findById(commentId)
                .orElseThrow(
                        () -> new NotFoundException(
                                ErrorCode.DATA_DOES_NOT_EXIST,
                                "상위 댓글을 찾을 수 없습니다."
                        )
                );

        return CommentResponseDto.from(
                postCommentRepository.save(
                        PostComment.of(
                                commentCreateRequestDto.content(),
                                userInfo.id(),
                                post,
                                Optional.ofNullable(commentCreateRequestDto.parentCommentId())
                                        .map(findParentComment)
                                        .orElse(null)
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public Pagination<CommentDetailResponseDto> findCommentsByPost(
            RequestUserInfo userInfo,
            String postId,
            Pageable pageable
    ) {
        Post post = findPostById(postId);

        validateBlockedUser(userInfo, post.getWriterId());

        return this.paginationFactory.create(
                postCommentRepositorySupport.findParentCommentByPost(post.getId(), userInfo.id(), pageable)
        );
    }

    @Transactional(readOnly = true)
    public Pagination<ChildCommentResponseDto> findAllChildCommentsByParent(
            RequestUserInfo userInfo,
            String parentId,
            Pageable pageable
    ) {
        PostComment postComment = findPostCommentById(parentId);


        validateBlockedUser(userInfo, postComment.getWriterId());

        return this.paginationFactory.create(
                postCommentRepositorySupport.findChildCommentByParentComment(postComment.getId(), userInfo.id(), pageable)
                        .map(childComment -> ChildCommentResponseDto.from(childComment, userInfo.id()))
        );
    }

    @Transactional
    public CommentResponseDto deleteComment(RequestUserInfo userInfo, String commentId) {
        PostComment postComment = findPostCommentById(commentId);

        IdEqualValidator.of(postComment.getWriterId(), userInfo.id()).validate();

        postComment.delete();

        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }

    @Transactional
    public CommentResponseDto updateComment(
            RequestUserInfo userInfo,
            String commentId,
            CommentUpdateRequestDto commentUpdateRequestDto
    ) {
        PostComment postComment = findPostCommentById(commentId);

        IdEqualValidator.of(postComment.getWriterId(), userInfo.id()).validate();

        postComment.updateContent(commentUpdateRequestDto.content());

        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }

    private Post findPostById(String postId) {
        return postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );
    }

    private PostComment findPostCommentById(String commentId) {
        return postCommentRepository.findByIdAndIsDeletedFalse(commentId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
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