package coLaon.ClaonBack.laon.Service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.laon.domain.Laon;
import coLaon.ClaonBack.laon.domain.LaonComment;
import coLaon.ClaonBack.laon.dto.CommentRequestDto;
import coLaon.ClaonBack.laon.dto.CommentResponseDto;
import coLaon.ClaonBack.laon.repository.LaonCommentRepository;
import coLaon.ClaonBack.laon.repository.LaonRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LaonCommentService {
    private final UserRepository userRepository;
    private final LaonCommentRepository laonCommentRepository;
    private final LaonRepository laonRepository;

    @Transactional
    public CommentResponseDto createComment(String userId, CommentRequestDto commentRequestDto) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        Laon laon = laonRepository.findById(commentRequestDto.getLaonId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );

        return CommentResponseDto.from(laonCommentRepository.save(
                LaonComment.of(commentRequestDto.getContent(), writer, laon,
                        commentRequestDto.getParentCommentId() != null ?
                                laonCommentRepository.findById(commentRequestDto.getParentCommentId()).orElseThrow(
                                        () -> new BadRequestException(
                                                ErrorCode.ROW_DOES_NOT_EXIST,
                                                "부모 댓글이 없습니다"
                                        )
                                ) : null)
                )
        );
    }
}
