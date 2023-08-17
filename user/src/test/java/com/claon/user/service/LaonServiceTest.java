package com.claon.user.service;

import com.claon.user.common.domain.Pagination;
import com.claon.user.common.domain.PaginationFactory;
import com.claon.user.common.exception.ErrorCode;
import com.claon.user.common.exception.UnauthorizedException;
import com.claon.user.domain.Laon;
import com.claon.user.domain.User;
import com.claon.user.dto.ClimbingHistoryResponseDto;
import com.claon.user.dto.HoldInfoResponseDto;
import com.claon.user.dto.LaonFindResponseDto;
import com.claon.user.dto.UserPostDetailResponseDto;
import com.claon.user.repository.BlockUserRepository;
import com.claon.user.repository.LaonRepository;
import com.claon.user.repository.LaonRepositorySupport;
import com.claon.user.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;
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
    BlockUserRepository blockUserRepository;
    @Mock
    LaonRepositorySupport laonRepositorySupport;
    @Mock
    PostPort postPort;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    LaonService laonService;

    private User user, laon;
    private Laon laonRelation;

    @BeforeEach
    void setUp() {
        this.laon = User.of(
                "test@gmail.com",
                "1234567890",
                "laonNickname",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.laon, "id", "laonId");

        this.user = User.of(
                "test@gmail.com",
                "1234567222",
                "userNickname",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.user, "id", "userId");

        this.laonRelation = Laon.of(
                this.user,
                this.laon
        );
    }

    @Test
    @DisplayName("Success case for create laon")
    void successCreateLaon() {
        try (MockedStatic<Laon> mockedLaon = mockStatic(Laon.class)) {
            // given
            given(this.userRepository.findById(user.getId())).willReturn(Optional.of(user));
            given(this.userRepository.findByNickname("userNickname")).willReturn(Optional.of(laon));
            given(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).willReturn(Optional.empty());
            given(this.blockUserRepository.findBlock(this.user.getId(), this.laon.getId())).willReturn(List.of());

            mockedLaon.when(() -> Laon.of(this.user, this.laon)).thenReturn(this.laonRelation);

            given(this.laonRepository.save(this.laonRelation)).willReturn(this.laonRelation);

            // when
            this.laonService.createLaon(user.getId(), "userNickname");

            // then
            assertThat(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).isNotNull();
        }
    }

    @Test
    @DisplayName("Failure case for create laon when laon myself")
    void failCreateLaonMyself() {
        //given
        given(this.userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(this.userRepository.findByNickname("userNickname")).willReturn(Optional.of(user));

        //when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.laonService.createLaon(user.getId(), "userNickname")
        );

        //then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("자신을 %s할 수 없습니다.", Laon.domain));
    }

    @Test
    @DisplayName("Success case for delete laon")
    void successDeleteLaon() {
        // given
        given(this.userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(this.userRepository.findByNickname("userNickname")).willReturn(Optional.of(laon));
        given(this.laonRepository.findByLaonIdAndUserId(this.laon.getId(), this.user.getId())).willReturn(Optional.of(this.laonRelation));

        // when
        this.laonService.deleteLaon(user.getId(), "userNickname");

        // then
        assertThat(this.laonRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Success case for find laons")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Page<LaonFindResponseDto> laonPage = new PageImpl<>(List.of(
                new LaonFindResponseDto(laonRelation.getLaon().getNickname(), laonRelation.getLaon().getImagePath())
        ), pageable, 2);

        given(this.laonRepositorySupport.findAllByUserId("userId", pageable)).willReturn(laonPage);

        // when
        Pagination<LaonFindResponseDto> laonFindResponseDto = this.laonService.findAllLaon(user.getId(), pageable);

        // then
        assertThat(laonFindResponseDto.getResults())
                .isNotNull()
                .extracting(LaonFindResponseDto::getLaonNickname, LaonFindResponseDto::getLaonProfileImage)
                .containsExactly(
                        tuple(this.laon.getNickname(), this.laon.getImagePath())
                );
    }

//    @Test
//    @DisplayName("Success case for find laon posts")
//    void successFindLaonPost() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//
//        Pagination<UserPostDetailResponseDto> postPagination = paginationFactory.create(
//                new PageImpl<>(List.of(
//                        UserPostDetailResponseDto.from(
//                                post1.getId(),
//                                post1.getCenter().getId(),
//                                post1.getCenter().getName(),
//                                post1.getWriter().getImagePath(),
//                                post1.getWriter().getNickname(),
//                                false,
//                                1,
//                                post1.getContent(),
//                                post1.getCreatedAt(),
//                                post1.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
//                                post1.getClimbingHistoryList().stream()
//                                        .map(history -> ClimbingHistoryResponseDto.from(
//                                                HoldInfoResponseDto.of(
//                                                        history.getHoldInfo().getId(),
//                                                        history.getHoldInfo().getName(),
//                                                        history.getHoldInfo().getImg(),
//                                                        history.getHoldInfo().getCrayonImageUrl()
//                                                ),
//                                                history.getClimbingCount()
//                                        ))
//                                        .collect(Collectors.toList())),
//                        UserPostDetailResponseDto.from(
//                                post2.getId(),
//                                post2.getCenter().getId(),
//                                post2.getCenter().getName(),
//                                post2.getWriter().getImagePath(),
//                                post2.getWriter().getNickname(),
//                                false,
//                                1,
//                                post2.getContent(),
//                                post2.getCreatedAt(),
//                                post2.getContentList().stream().map(PostContents::getUrl).collect(Collectors.toList()),
//                                post2.getClimbingHistoryList().stream()
//                                        .map(history -> ClimbingHistoryResponseDto.from(
//                                                HoldInfoResponseDto.of(
//                                                        history.getHoldInfo().getId(),
//                                                        history.getHoldInfo().getName(),
//                                                        history.getHoldInfo().getImg(),
//                                                        history.getHoldInfo().getCrayonImageUrl()
//                                                ),
//                                                history.getClimbingCount()
//                                        ))
//                                        .collect(Collectors.toList())))
//                        , pageable, 2)
//        );
//        given(this.postPort.findLaonPost(user, pageable)).willReturn(postPagination);
//
//        // when
//        Pagination<UserPostDetailResponseDto> post = this.laonService.findLaonPost(user.getId(), pageable);
//
//        //then
//        assertThat(post.getResults())
//                .isNotNull();
//                .extracting(UserPostDetailResponseDto::getPostId, UserPostDetailResponseDto::getContent)
//                .contains(
//                        tuple("testPostId", post1.getContent()),
//                        tuple("testPostId2", post2.getContent())
//                );
//    }
}