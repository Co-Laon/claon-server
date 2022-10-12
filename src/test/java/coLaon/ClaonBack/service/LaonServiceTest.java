package coLaon.ClaonBack.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.user.domain.Laon;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.ClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.HoldInfoResponseDto;
import coLaon.ClaonBack.user.dto.LaonFindResponseDto;
import coLaon.ClaonBack.user.dto.UserPostDetailResponseDto;
import coLaon.ClaonBack.user.repository.LaonRepository;
import coLaon.ClaonBack.user.repository.LaonRepositorySupport;
import coLaon.ClaonBack.user.repository.UserRepository;
import coLaon.ClaonBack.user.service.LaonService;
import coLaon.ClaonBack.user.service.PostPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class LaonServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    LaonRepository laonRepository;
    @Mock
    LaonRepositorySupport laonRepositorySupport;
    @Mock
    PostPort postPort;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    LaonService laonService;

    private User user, user2, laon, laon2;
    private Laon laonRelation, laonRelation2;
    private Center center;
    private Post post1, post2;

    @BeforeEach
    void setUp() {
        this.laon = User.of(
                "test@gmail.com",
                "1234567890",
                "laonNickname1",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.laon, "id", "laonId");

        this.laon2 = User.of(
                "test1@gmail.com",
                "12345678902",
                "laonNickname2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId3"
        );
        ReflectionTestUtils.setField(this.laon2, "id", "laonId2");

        HoldInfo holdInfo = HoldInfo.of(
                "holdName1",
                "/hold1.png",
                center
        );
        ReflectionTestUtils.setField(holdInfo, "id", "holdId1");

        this.center = Center.of(
                "testCenter",
                "testAddress",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test"
        );
        ReflectionTestUtils.setField(this.center, "id", "center1");

        ClimbingHistory climbingHistory = ClimbingHistory.of(
                this.post1,
                holdInfo,
                0
        );
        ReflectionTestUtils.setField(climbingHistory, "id", "climbingId");

        this.user = User.of(
                "test@gmail.com",
                "1234567222",
                "userNickname2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.user, "id", "userId");

        this.user2 = User.of(
                "test123@gmail.com",
                "test2345!!",
                "test2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.user2, "id", "testUserId2");

        this.laonRelation = Laon.of(
                this.user,
                this.laon
        );

        this.laonRelation2 = Laon.of(
                this.user,
                this.laon2
        );

        this.post1 = Post.of(
                center,
                "testContent1",
                user,
                List.of(PostContents.of(
                        "test.com/test.png"
                )),
                Set.of()
        );
        ReflectionTestUtils.setField(this.post1, "id", "testPostId");
        ReflectionTestUtils.setField(this.post1, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post1, "updatedAt", LocalDateTime.now());

        this.post2 = Post.of(
                center,
                "testContent2",
                user2,
                List.of(PostContents.of(
                        "test2.com/test.png"
                )),
                Set.of(climbingHistory)
        );
        ReflectionTestUtils.setField(this.post2, "id", "testPostId2");
        ReflectionTestUtils.setField(this.post2, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post2, "updatedAt", LocalDateTime.now());

    }

    @Test
    @DisplayName("Success case for create laon")
    void successCreateLaon() {
        try (MockedStatic<Laon> mockedLaon = mockStatic(Laon.class)) {
            // given
            given(this.userRepository.findByNickname("userNickname1")).willReturn(Optional.of(laon));
            given(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).willReturn(Optional.empty());

            mockedLaon.when(() -> Laon.of(this.user, this.laon)).thenReturn(this.laonRelation);

            given(this.laonRepository.save(this.laonRelation)).willReturn(this.laonRelation);

            // when
            this.laonService.createLaon(user, "userNickname1");

            // then
            assertThat(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).isNotNull();
        }
    }

    @Test
    @DisplayName("Failure case for create laon when laon myself")
    void failCreateLaonMyself() {
        //given
        given(this.userRepository.findByNickname("userNickname1")).willReturn(Optional.of(user));

        //when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.laonService.createLaon(user, "userNickname1")
        );

        //then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("자기 자신은 %s이 불가능합니다.", Laon.domain));
    }

    @Test
    @DisplayName("Success case for delete laon")
    void successDeleteLaon() {
        // given
        given(this.userRepository.findByNickname("userNickname1")).willReturn(Optional.of(laon));
        given(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).willReturn(Optional.of(this.laonRelation));

        // when
        this.laonService.deleteLaon(user, "userNickname1");

        // then
        assertThat(this.laonRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Success case for find laons")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);

        Page<Laon> laons = new PageImpl<>(List.of(laonRelation, laonRelation2), pageable, 2);

        given(this.laonRepositorySupport.findAllByUserId("userId", pageable)).willReturn(laons);

        // when
        Pagination<LaonFindResponseDto> laonFindResponseDto = this.laonService.findAllLaon(user, pageable);

        // then
        assertThat(laonFindResponseDto.getResults())
                .isNotNull()
                .extracting(LaonFindResponseDto::getLaonNickname, LaonFindResponseDto::getLaonProfileImage)
                .containsExactly(
                        tuple(this.laon.getNickname(), this.laon.getImagePath()),
                        tuple(this.laon2.getNickname(), this.laon2.getImagePath())
                );
    }

    @Test
    @DisplayName("Success case for find laon posts")
    void successFindLaonPost() {
        // given
        Pageable pageable = PageRequest.of(0, 2);

        Pagination<UserPostDetailResponseDto> postPagination = paginationFactory.create(
                new PageImpl<>(List.of(
                        UserPostDetailResponseDto.from(
                                post1.getId(),
                                post1.getCenter().getId(),
                                post1.getCenter().getName(),
                                post1.getWriter().getImagePath(),
                                post1.getWriter().getNickname(),
                                1,
                                post1.getContent(),
                                post1.getCreatedAt(),
                                post1.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                                post1.getClimbingHistorySet().stream()
                                        .map(history -> ClimbingHistoryResponseDto.from(
                                                HoldInfoResponseDto.of(
                                                        history.getHoldInfo().getId(),
                                                        history.getHoldInfo().getName(),
                                                        history.getHoldInfo().getImg(),
                                                        history.getHoldInfo().getCrayonImageUrl()
                                                ),
                                                history.getClimbingCount()
                                        ))
                                        .collect(Collectors.toList())),
                        UserPostDetailResponseDto.from(
                                post2.getId(),
                                post2.getCenter().getId(),
                                post2.getCenter().getName(),
                                post2.getWriter().getImagePath(),
                                post2.getWriter().getNickname(),
                                1,
                                post2.getContent(),
                                post2.getCreatedAt(),
                                post2.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
                                post2.getClimbingHistorySet().stream()
                                        .map(history -> ClimbingHistoryResponseDto.from(
                                                HoldInfoResponseDto.of(
                                                        history.getHoldInfo().getId(),
                                                        history.getHoldInfo().getName(),
                                                        history.getHoldInfo().getImg(),
                                                        history.getHoldInfo().getCrayonImageUrl()
                                                ),
                                                history.getClimbingCount()
                                        ))
                                        .collect(Collectors.toList())))
                , pageable, 2)
        );
        given(this.postPort.findLaonPost(user, pageable)).willReturn(postPagination);

        // when
        Pagination<UserPostDetailResponseDto> post = this.laonService.findLaonPost(user, pageable);

        //then
        assertThat(post.getResults())
                .isNotNull()
                .extracting(UserPostDetailResponseDto::getPostId, UserPostDetailResponseDto::getContent)
                .contains(
                        tuple("testPostId", post1.getContent()),
                        tuple("testPostId2", post2.getContent())
                );
    }
}