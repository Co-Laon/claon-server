package coLaon.ClaonBack.post.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.IdEqualValidator;
import coLaon.ClaonBack.common.validator.IsPrivateValidator;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
import coLaon.ClaonBack.post.dto.ChildCommentResponseDto;
import coLaon.ClaonBack.post.repository.PostCommentRepository;
import coLaon.ClaonBack.post.repository.PostCommentRepositorySupport;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
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
    private final BlockUserRepository blockUserRepository;

    @Transactional
    public CommentResponseDto createComment(
            User user,
            String postId,
            CommentCreateRequestDto commentCreateRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriter().getNickname().equals(user.getNickname())) {
            IsPrivateValidator.of(post.getWriter().getNickname(), post.getWriter().getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", post.getWriter().getNickname())
                );
            }
        }

        return CommentResponseDto.from(
                postCommentRepository.save(
                        PostComment.of(
                                commentCreateRequestDto.getContent(),
                                user,
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
            User user,
            String postId,
            Pageable pageable
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriter().getNickname().equals(user.getNickname())) {
            IsPrivateValidator.of(post.getWriter().getNickname(), post.getWriter().getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", post.getWriter().getNickname())
                );
            }
        }

        return this.paginationFactory.create(
                postCommentRepositorySupport.findParentCommentByPost(post.getId(), user.getId(), user.getNickname(), pageable)
        );
    }

    @Transactional(readOnly = true)
    public Pagination<ChildCommentResponseDto> findAllChildCommentsByParent(
            User user,
            String parentId,
            Pageable pageable
    ) {
        PostComment postComment = postCommentRepository.findByIdAndIsDeletedFalse(parentId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
                )
        );

        if (!postComment.getPost().getWriter().getNickname().equals(user.getNickname())) {
            IsPrivateValidator.of(postComment.getPost().getWriter().getNickname(), postComment.getPost().getWriter().getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(user.getId(), postComment.getPost().getWriter().getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", postComment.getPost().getWriter().getNickname())
                );
            }
        }

        return this.paginationFactory.create(
                postCommentRepositorySupport.findChildCommentByParentComment(postComment.getId(), user.getId(), pageable)
                        .map(childComment -> ChildCommentResponseDto.from(childComment, user.getNickname()))
        );
    }

    @Transactional
    public CommentResponseDto deleteComment(User user, String commentId) {
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(postComment.getWriter().getId(), user.getId()).validate();

        postComment.delete();

        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }

    @Transactional
    public CommentResponseDto updateComment(
            User user,
            String commentId,
            CommentUpdateRequestDto commentUpdateRequestDto
    ) {
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(postComment.getWriter().getId(), user.getId()).validate();

        postComment.updateContent(commentUpdateRequestDto.getContent());

        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }
}