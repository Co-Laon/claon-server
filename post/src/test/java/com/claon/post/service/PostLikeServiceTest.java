package com.claon.post.service;

import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.domain.BlockUser;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostLike;
import com.claon.post.dto.LikeFindResponseDto;
import com.claon.post.repository.BlockUserRepository;
import com.claon.post.repository.PostLikeRepository;
import com.claon.post.repository.PostLikeRepositorySupport;
import com.claon.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostLikeServiceTest {
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    PostLikeRepositorySupport postLikeRepositorySupport;
    @Mock
    BlockUserRepository blockUserRepository;

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostLikeService postLikeService;

    private final RequestUserInfo USER_INFO = new RequestUserInfo("USER_ID");
    private PostLike postLike;
    private Post post, blockedPost;
    private BlockUser blockUser;

    @BeforeEach
    void setUp() {
        post = Post.of(
                "CENTER_ID",
                "testContent",
                USER_INFO.id(),
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(post, "id", "testPostId");
        ReflectionTestUtils.setField(post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());

        postLike = PostLike.of(
                USER_INFO.id(),
                post
        );
        ReflectionTestUtils.setField(postLike, "id", "testPostLikeId");

        blockUser = BlockUser.of(
                USER_INFO.id(),
                "BLOCKED_ID"
        );

        blockedPost = Post.of(
                "centerId",
                "testContent3",
                "BLOCKED_ID",
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(blockedPost, "id", "blockedPostId");
        ReflectionTestUtils.setField(blockedPost, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(blockedPost, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Success case for create like")
    void successCreateLike() {
        try (MockedStatic<PostLike> mockedPostLike = mockStatic(PostLike.class)) {
            // given
            given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));
            given(postLikeRepository.findByLikerIdAndPost(USER_INFO.id(), post)).willReturn(Optional.empty());

            mockedPostLike.when(() -> PostLike.of(USER_INFO.id(), post)).thenReturn(postLike);
            given(postLikeRepository.countByPost(post)).willReturn(1);

            given(postLikeRepository.save(postLike)).willReturn(postLike);

            // when
            var likeResponseDto = postLikeService.createLike(USER_INFO, post.getId());

            // then
            assertThat(likeResponseDto)
                    .isNotNull()
                    .extracting("postId", "likeCount")
                    .contains(post.getId(), 1);
        }
    }

    @Test
    @DisplayName("Success case for delete like")
    void successDeleteLike() {
        // given
        given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));
        given(postLikeRepository.findByLikerIdAndPost(USER_INFO.id(), post)).willReturn(Optional.of(postLike));

        // when
        var likeResponseDto = postLikeService.deleteLike(USER_INFO, post.getId());

        // then
        assertThat(likeResponseDto)
                .isNotNull()
                .extracting("postId", "likeCount")
                .contains(post.getId(), 0);
    }

    @Test
    @DisplayName("Success case for find likes")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(postRepository.findByIdAndIsDeletedFalse(post.getId())).willReturn(Optional.of(post));

        given(postLikeRepositorySupport.findAllByPost(post.getId(), USER_INFO.id(), pageable)).willReturn(new PageImpl<>(List.of(postLike), pageable, 2));

        // when
        var likeFindResponseDto = postLikeService.findLikeByPost(USER_INFO, post.getId(), pageable);

        // then
        assertThat(likeFindResponseDto.getResults())
                .isNotNull()
                .extracting(LikeFindResponseDto::getPostId, LikeFindResponseDto::getLikerId)
                .contains(
                        tuple(post.getId(), postLike.getLikerId())
                );
    }

    @Test
    @DisplayName("Failure case for find likes when blocked user")
    void failFindLikesBlockedUser() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(postRepository.findByIdAndIsDeletedFalse(blockedPost.getId())).willReturn(Optional.of(blockedPost));
        given(blockUserRepository.findBlock(USER_INFO.id(), blockUser.getBlockedUserId())).willReturn(List.of(blockUser));

        // when
        final UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> postLikeService.findLikeByPost(USER_INFO, "blockedPostId", pageable)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriterId()));
    }
}
