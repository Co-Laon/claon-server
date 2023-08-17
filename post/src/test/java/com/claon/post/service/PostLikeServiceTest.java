package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.exception.ErrorCode;
import com.claon.post.common.exception.UnauthorizedException;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostLike;
import com.claon.post.dto.LikeFindResponseDto;
import com.claon.post.dto.LikeResponseDto;
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
import org.springframework.data.domain.Page;
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

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostLikeService postLikeService;

    private final String USER_ID = "USER_ID";
    private final String CENTER_ID = "CENTER_ID";
    private PostLike postLike;
    private Post post, blockedPost, privatePost;

    @BeforeEach
    void setUp() {
        this.post = Post.of(
                CENTER_ID,
                "testContent1",
                USER_ID,
                List.of(),
                List.of()
        );
        ReflectionTestUtils.setField(this.post, "id", "testPostId");
        ReflectionTestUtils.setField(this.post, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(this.post, "updatedAt", LocalDateTime.now());

        this.postLike = PostLike.of(
                USER_ID,
                post
        );
        ReflectionTestUtils.setField(this.postLike, "id", "testPostLikeId");

//        this.blockedPost = Post.of(
//                center,
//                "testContent3",
//                blockedUser,
//                List.of(),
//                List.of()
//        );
//        ReflectionTestUtils.setField(this.blockedPost, "id", "blockedPostId");
//        ReflectionTestUtils.setField(this.blockedPost, "createdAt", LocalDateTime.now());
//        ReflectionTestUtils.setField(this.blockedPost, "updatedAt", LocalDateTime.now());
//
//        this.privatePost = Post.of(
//                center,
//                "testContent4",
//                privateUser,
//                List.of(),
//                List.of()
//        );
//        ReflectionTestUtils.setField(this.privatePost, "id", "privatePostId");
//        ReflectionTestUtils.setField(this.privatePost, "createdAt", LocalDateTime.now());
//        ReflectionTestUtils.setField(this.privatePost, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Success case for create like")
    void successCreateLike() {
        try (MockedStatic<PostLike> mockedPostLike = mockStatic(PostLike.class)) {
            // given
            given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
            given(this.postLikeRepository.findByLikerIdAndPost(USER_ID, post)).willReturn(Optional.empty());

            mockedPostLike.when(() -> PostLike.of(USER_ID, post)).thenReturn(postLike);
            given(this.postLikeRepository.countByPost(post)).willReturn(1);

            given(this.postLikeRepository.save(this.postLike)).willReturn(postLike);

            // when
            LikeResponseDto likeResponseDto = this.postLikeService.createLike(USER_ID, "testPostId");

            // then
            assertThat(likeResponseDto)
                    .isNotNull()
                    .extracting("postId", "likeCount")
                    .contains("testPostId", 1);
        }
    }

    @Test
    @DisplayName("Success case for delete like")
    void successDeleteLike() {
        // given
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));
        given(this.postLikeRepository.findByLikerIdAndPost(USER_ID, post)).willReturn(Optional.of(postLike));

        // when
        LikeResponseDto likeResponseDto = this.postLikeService.deleteLike(USER_ID, "testPostId");

        // then
        assertThat(likeResponseDto)
                .isNotNull()
                .extracting("postId", "likeCount")
                .contains("testPostId", 0);
    }

    @Test
    @DisplayName("Success case for find likes")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findByIdAndIsDeletedFalse("testPostId")).willReturn(Optional.of(post));

        Page<PostLike> postLikes = new PageImpl<>(List.of(postLike), pageable, 2);

        given(this.postLikeRepositorySupport.findAllByPost(post.getId(), USER_ID, pageable)).willReturn(postLikes);

        // when
        Pagination<LikeFindResponseDto> likeFindResponseDto = this.postLikeService.findLikeByPost(USER_ID, "testPostId", pageable);

        // then
        assertThat(likeFindResponseDto.getResults())
                .isNotNull()
                .extracting(LikeFindResponseDto::getPostId, LikeFindResponseDto::getLikerId)
                .contains(
                        tuple("testPostId", postLike.getLikerId())
                );
    }

//    @Test
//    @DisplayName("Success case for find likes of own post")
//    void successFindOwnPostLikes() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//
//        Page<PostLike> postLikes = new PageImpl<>(List.of(postLike, postLike2), pageable, 2);
//
//        given(this.postLikeRepositorySupport.findAllByPost(privatePost.getId(), privateUser.getId(), pageable)).willReturn(postLikes);
//
//        // when
//        Pagination<LikeFindResponseDto> likeFindResponseDto = this.postLikeService.findLikeByPost(privateUser, "privatePostId", pageable);
//
//        // then
//        assertThat(likeFindResponseDto.getResults())
//                .isNotNull()
//                .extracting(LikeFindResponseDto::getPostId, LikeFindResponseDto::getLikerNickname)
//                .contains(
//                        tuple("testPostId", postLike.getLiker().getNickname()),
//                        tuple("testPostId", postLike2.getLiker().getNickname())
//                );
//    }

//    @Test
//    @DisplayName("Failure case for find likes of private post")
//    void failFindLikesPrivatePost() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//        given(this.postRepository.findByIdAndIsDeletedFalse("privatePostId")).willReturn(Optional.of(privatePost));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> this.postLikeService.findLikeByPost(user, "privatePostId", pageable)
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s은 비공개 상태입니다.", privatePost.getWriter().getNickname()));
//    }

//    @Test
//    @DisplayName("Failure case for find likes when blocked user")
//    void failFindLikesBlockedUser() {
//        // given
//        Pageable pageable = PageRequest.of(0, 2);
//        given(this.postRepository.findByIdAndIsDeletedFalse("blockedPostId")).willReturn(Optional.of(blockedPost));
//        given(this.blockUserRepository.findBlock("testUserId", blockedUser.getId())).willReturn(List.of(blockUser));
//
//        // when
//        final UnauthorizedException ex = assertThrows(
//                UnauthorizedException.class,
//                () -> this.postLikeService.findLikeByPost(user, "blockedPostId", pageable)
//        );
//
//        // then
//        assertThat(ex)
//                .extracting("errorCode", "message")
//                .contains(ErrorCode.NOT_ACCESSIBLE, String.format("%s을 찾을 수 없습니다.", blockedPost.getWriter().getNickname()));
//    }
}
