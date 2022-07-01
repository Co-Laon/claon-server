package coLaon.ClaonBack.post.Service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostComment;
import coLaon.ClaonBack.post.dto.CommentRequestDto;
import coLaon.ClaonBack.post.dto.CommentResponseDto;
import coLaon.ClaonBack.post.repository.PostCommentRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto createComment(String userId, CommentRequestDto commentRequestDto) {
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
}
