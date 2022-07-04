package coLaon.ClaonBack.post.Service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.dto.CommentCreateRequestDto;
import coLaon.ClaonBack.post.dto.CommentFindResponseDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.dto.CommentUpdateRequestDto;
import coLaon.ClaonBack.post.repository.PostCommentRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto createComment(String userId, CommentCreateRequestDto commentRequestDto) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        Post post = postRepository.findById(commentRequestDto.getPostId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );

        return CommentResponseDto.from(postCommentRepository.save(
                PostComment.of(commentRequestDto.getContent(), writer, post,
                        commentRequestDto.getParentCommentId() != null ?
                                postCommentRepository.findById(commentRequestDto.getParentCommentId()).orElseThrow(
                                        () -> new BadRequestException(
                                                ErrorCode.ROW_DOES_NOT_EXIST,
                                                "부모 댓글이 없습니다"
                                        )
                                ) : null)
                )
        );
    }

    @Transactional
    public List<CommentFindResponseDto> findCommentsByPost(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );
        List<CommentFindResponseDto> result = new ArrayList<>();

        postCommentRepository.findByPostAndParentCommentIsNullOrderByCreatedAt(post).forEach(parent -> {
            System.out.println(parent.getContent());
            CommentFindResponseDto parentCommentDto = CommentFindResponseDto.from(parent);
            postCommentRepository.findFirst3ByParentCommentIdOrderByCreatedAt(
                    parentCommentDto.getCommentId()).forEach(child -> {
                CommentFindResponseDto childCommentDto = CommentFindResponseDto.from(child);
                parentCommentDto.getChildren().add(childCommentDto);
            });
            result.add(parentCommentDto);
        });
        return result;
    }

    @Transactional
    public List<CommentFindResponseDto> findAllChildCommentsByParent(String parentId) {
        PostComment postComment = postCommentRepository.findById(parentId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "댓글 정보가 없습니다."
                )
        );

        return postCommentRepository.findAllByParentCommentOrderByCreatedAt(postComment).stream().map(
                CommentFindResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto deleteComment(String commentId, String userId) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "댓글 정보가 없습니다."
                )
        );

        if (!postComment.getWriter().equals(writer)) throw new BadRequestException(
                ErrorCode.NOT_ACCESSIBLE,
                "로그인된 사용자가 작성한 댓글이 아닙니다"
        );

        if (postComment.getParentComment() == null) {
            postCommentRepository.findAllByParentCommentOrderByCreatedAt(postComment).forEach(child-> {
                child.deleteContent();
                postCommentRepository.save(child);
            });
        }
        postComment.deleteContent();
        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }

    @Transactional
    public CommentResponseDto updateComment(String userId, CommentUpdateRequestDto commentUpdateRequestDto) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );
        PostComment postComment = postCommentRepository.findById(commentUpdateRequestDto.getCommentId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "댓글 정보가 없습니다."
                )
        );
        if (!postComment.getWriter().equals(writer)) throw new BadRequestException(
                ErrorCode.NOT_ACCESSIBLE,
                "로그인된 사용자가 작성한 댓글이 아닙니다"
        );
        postComment.updateContent(commentUpdateRequestDto.getContent());
        return CommentResponseDto.from(postCommentRepository.save(postComment));
    }
}

