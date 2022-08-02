package coLaon.ClaonBack.post.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.validator.IdEqualValidator;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
import coLaon.ClaonBack.post.dto.ChildCommentResponseDto;
import coLaon.ClaonBack.post.repository.PostCommentRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final BlockUserRepository blockUserRepository;
    private final PaginationFactory paginationFactory;

    @Transactional
    public CommentResponseDto createComment(
            String userId,
            String postId,
            CommentCreateRequestDto commentCreateRequestDto
    ) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        return CommentResponseDto.from(
                postCommentRepository.save(
                        PostComment.of(
                                commentCreateRequestDto.getContent(),
                                writer,
                                post,
                                Optional.ofNullable(commentCreateRequestDto.getParentCommentId())
                                        .map(parentCommentId ->
                                                postCommentRepository.findById(commentCreateRequestDto.getParentCommentId())
                                                        .orElseThrow(
                                                                () -> new BadRequestException(
                                                                        ErrorCode.ROW_DOES_NOT_EXIST,
                                                                        "상위 댓글을 찾을 수 없습니다."
                                                                )))
                                        .orElse(null)
                        )
                ));
    }

    @Transactional(readOnly = true)
    public Pagination<CommentFindResponseDto> findCommentsByPost(String userId, String postId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        return this.paginationFactory.create(
                postCommentRepository.findByPostAndParentCommentIsNullAndIsDeletedFalse(post, pageable)
                        .map(parent ->
                                blockUserRepository.findBlock(user.getId(), parent.getWriter().getId()).isEmpty() ?
                                        CommentFindResponseDto.from(
                                                parent,
                                                postCommentRepository.findTop3ByParentCommentAndIsDeletedFalseOrderByCreatedAt(parent),
                                                postCommentRepository.countAllByParentCommentAndIsDeletedFalse(parent)) : null
                        ));
    }

    @Transactional(readOnly = true)
    public Pagination<ChildCommentResponseDto> findAllChildCommentsByParent(String userId, String parentId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        PostComment postComment = postCommentRepository.findByIdAndIsDeletedFalse(parentId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "댓글 정보가 없습니다."
                )
        );

        return this.paginationFactory.create(
                postCommentRepository.findAllByParentCommentAndIsDeletedFalse(postComment, pageable)
                        .map(child -> blockUserRepository.findBlock(user.getId(), child.getWriter().getId()).isEmpty() ?
                                ChildCommentResponseDto.from(child) : null
                        ));
    }

    @Transactional
    public CommentResponseDto deleteComment(String userId, String commentId) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(postComment.getWriter().getId(), writer.getId()).validate();

        postComment.delete();

        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }

    @Transactional
    public CommentResponseDto updateComment(
            String userId,
            String commentId,
            CommentUpdateRequestDto commentUpdateRequestDto
    ) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "댓글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(postComment.getWriter().getId(), writer.getId()).validate();

        postComment.updateContent(commentUpdateRequestDto.getContent());

        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }
}