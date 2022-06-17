package coLaon.ClaonBack;

import coLaon.ClaonBack.laon.Service.LaonCommentService;
import coLaon.ClaonBack.laon.domain.Laon;
import coLaon.ClaonBack.laon.domain.LaonComment;
import coLaon.ClaonBack.laon.dto.CommentRequestDto;
import coLaon.ClaonBack.laon.dto.CommentResponseDto;
import coLaon.ClaonBack.laon.repository.LaonCommentRepository;
import coLaon.ClaonBack.laon.repository.LaonRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class LaonCommentServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    LaonCommentRepository laonCommentRepository;
    @Mock
    LaonRepository laonRepository;

    @InjectMocks
    LaonCommentService laonCommentService;

    private LaonComment laonComment;
    private User writer;
    private Laon laon;
    private LaonComment childLaonComment;

    @BeforeEach
    void setUp() {
        this.writer = User.of(
                "testUserId",
                "01012341234",
                "test@gmail.com",
                "test1234!!",
                "test",
                "경기도",
                "성남시",
                "",
                "instagramId"
        );

        this.laon = Laon.of(
                "testLaonId",
                "center1",
                "wall",
                "hold",
                "testUrl",
                null,
                "test",
                writer
        );

        this.laonComment = LaonComment.of(
                "testCommentId",
                "testContent1",
                writer,
                laon,
                null
        );

        this.childLaonComment = LaonComment.of(
                "testchildContent1",
                writer,
                laon,
                laonComment
        );
    }


    @Test
    @DisplayName("Success case for create parent comment")
    void successCreateParentComment() {
        try (MockedStatic<LaonComment> mockedLaonComment = mockStatic(LaonComment.class)) {
            //given
            CommentRequestDto commentRequestDto = new CommentRequestDto("testContent1", null, "testLaonId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.laonRepository.findById("testLaonId")).willReturn(Optional.of(laon));

            given(LaonComment.of("testContent1", this.writer, this.laon, null)).willReturn(this.laonComment);

            given(this.laonCommentRepository.save(this.laonComment)).willReturn(this.laonComment);
            //when
            CommentResponseDto commentResponseDto = this.laonCommentService.createComment("testUserId", commentRequestDto);
            //then
            assertThat(commentResponseDto).isNotNull();
            assertThat(commentResponseDto.getContent()).isEqualTo("testContent1");
        }
    }

    @Test
    @DisplayName("Success case for create child comment")
    void successCreateChildComment() {
        try (MockedStatic<LaonComment> mockedLaonComment = mockStatic(LaonComment.class)) {
            //given
            CommentRequestDto commentRequestDto = new CommentRequestDto("testchildContent1", laonComment.getId(), "testLaonId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(writer));
            given(this.laonRepository.findById("testLaonId")).willReturn(Optional.of(laon));
            given(this.laonCommentRepository.findById("testCommentId")).willReturn(Optional.of(laonComment));

            given(LaonComment.of("testchildContent1", this.writer, this.laon, laonComment)).willReturn(this.childLaonComment);

            given(this.laonCommentRepository.save(this.childLaonComment)).willReturn(this.childLaonComment);
            //when
            CommentResponseDto commentResponseDto = this.laonCommentService.createComment("testUserId", commentRequestDto);
            //then
            assertThat(commentResponseDto).isNotNull();
            assertThat(commentResponseDto.getContent()).isEqualTo("testchildContent1");
        }
    }

}
