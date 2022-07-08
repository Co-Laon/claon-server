package coLaon.ClaonBack.post.service;

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
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto createComment(String userId, CommentCreateRequestDto commentCreateRequestDto) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );
        Post post = postRepository.findById(commentCreateRequestDto.getPostId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );

        return CommentResponseDto.from(postCommentRepository.save(
                        PostComment.of(commentCreateRequestDto.getContent(), writer, post,
                                commentCreateRequestDto.getParentCommentId() != null ?
                                        postCommentRepository.findById(commentCreateRequestDto.getParentCommentId()).orElseThrow(
                                                () -> new BadRequestException(
                                                        ErrorCode.ROW_DOES_NOT_EXIST,
                                                        "부모 댓글이 없습니다."
                                                )
                                        ) : null)
                )
        );
    }

    @Transactional(readOnly = true)
    public List<CommentFindResponseDto> findCommentsByPost(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );

        return postCommentRepository.findByPostAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAt(post)
                .stream()
                .map(parent ->
                        CommentFindResponseDto.from(
                                parent,
                                postCommentRepository.findFirstThreeByParentCommentIdAndIsDeletedFalseOrderByCreatedAt(parent.getId())
                        )
                ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChildCommentResponseDto> findAllChildCommentsByParent(String parentId) {
        PostComment postComment = postCommentRepository.findById(parentId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "댓글 정보가 없습니다."
                )
        );

        return postCommentRepository.findAllByParentCommentAndIsDeletedFalseOrderByCreatedAt(postComment).stream()
                .map(ChildCommentResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto deleteComment(String commentId, String userId) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "댓글 정보가 없습니다."
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
                        "댓글 정보가 없습니다."
                )
        );
        IdEqualValidator.of(postComment.getWriter().getId(), writer.getId()).validate();

        postComment.updateContent(commentUpdateRequestDto.getContent());
        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }
}
