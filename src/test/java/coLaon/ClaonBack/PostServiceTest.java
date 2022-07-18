package coLaon.ClaonBack;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.service.PostService;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeFindResponseDto;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.post.dto.LikeRequestDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.dto.PostContentsDto;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.repository.PostContentsRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    PostContentsRepository postContentsRepository;
    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    PostService postService;

    private PostLike postLike;
    private PostLike postLike2;
    private User user;
    private User user2;
    private Post post;
    private PostContents postContents;

    @BeforeEach
    void setUp() {
        this.user = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                "경기도",
                "성남시",
                "",
                "",
                "instagramId"
        );

        this.user2 = User.of(
                "testUserId2",
                "test123@gmail.com",
                "test2345!!",
                "test2",
                "경기도",
                "성남시",
                "",
                "instagramId2"
        );

        this.post = Post.of(
                "testPostId",
                "center1",
                "hold1",
                "testContent1",
                user,
                Set.of()
        );

        this.postContents = PostContents.of(
                "testPostContentsId",
                post,
                "test.com/test.png"
        );

        this.postLike = PostLike.of(
                "testPostLikeId",
                user,
                post
        );

        this.postLike2 = PostLike.of(
                "testPostLikeId2",
                user2,
                post
        );
    }

    @Test
    @DisplayName("Success case for create post")
    void successCreatePost() {
        MockedStatic<Post> mockedPost = mockStatic(Post.class);
        MockedStatic<PostContents> mockedPostContents = mockStatic(PostContents.class);

        // given
        PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(
                "center1",
                "hold",
                "testContent",
                List.of(new PostContentsDto("test.com/test.png"))
        );

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        mockedPost.when(() ->
                        Post.of(
                                postCreateRequestDto.getCenterName(),
                                postCreateRequestDto.getHoldInfo(),
                                postCreateRequestDto.getContent(),
                                user
                        ))
                .thenReturn(post);
        given(this.postRepository.save(post)).willReturn(post);

        mockedPostContents.when(() ->
                        PostContents.of(
                                post.getId(),
                                post,
                                "test.com/test.png"
                        ))
                .thenReturn(postContents);
        given(this.postContentsRepository.save(postContents)).willReturn(postContents);

        // when
        PostResponseDto postResponseDto = this.postService.createPost("testUserId", postCreateRequestDto);

        // then
        assertThat(postResponseDto).isNotNull();
        assertThat(postCreateRequestDto.getCenterName()).isEqualTo(postResponseDto.getCenterName());
        assertThat(postCreateRequestDto.getContentsList().size()).isEqualTo(1);

        mockedPost.close();
        mockedPostContents.close();
    }

    @Test
    @DisplayName("Failure case when invalid image format for create post")
    void failCreatePost_InvalidImageFormat() {
        // given
        List<PostContentsDto> postContentsDtoList = Stream.of("png", "jpg", "gif")
                .map(s -> "test.com/test." + s)
                .map(PostContentsDto::new)
                .collect(Collectors.toList());

        PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(
                "center1",
                "hold",
                "testContent",
                postContentsDtoList
        );

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));

        // when
        final BadRequestException ex = Assertions.assertThrows(
                BadRequestException.class,
                () -> postService.createPost("testUserId", postCreateRequestDto)
        );

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_FORMAT);
    }

    @Test
    @DisplayName("Success case for create like")
    void successCreateLike() {
        try (MockedStatic<PostLike> mockedPostLike = mockStatic(PostLike.class)) {
            // given
            LikeRequestDto likeRequestDto = new LikeRequestDto("testPostId");

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
            given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));
            given(this.postLikeRepository.findByLikerAndPost(user, post)).willReturn(Optional.empty());

            mockedPostLike.when(() -> PostLike.of(user, post)).thenReturn(postLike);
            given(this.postLikeRepository.countByPost(post)).willReturn(1);

            given(this.postLikeRepository.save(this.postLike)).willReturn(postLike);

            // when
            LikeResponseDto likeResponseDto = this.postService.createLike("testUserId", likeRequestDto);

            // then
            assertThat(likeResponseDto).isNotNull();
            assertThat(likeResponseDto.getLikeNumber()).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Success case for delete like")
    void successDeleteLike() {
        // given
        LikeRequestDto likeRequestDto = new LikeRequestDto("testPostId");

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));
        given(this.postLikeRepository.findByLikerAndPost(user, post)).willReturn(Optional.of(postLike));

        // when
        LikeResponseDto likeResponseDto = this.postService.deleteLike("testUserId", likeRequestDto);

        // then
        assertThat(likeResponseDto).isNotNull();
        assertThat(likeRequestDto.getPostId()).isEqualTo(likeResponseDto.getPostId());
    }

    @Test
    @DisplayName("Success case for find likes")
    void successFindLikes() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

        Page<PostLike> postLikes = new PageImpl<>(List.of(postLike, postLike2), pageable, 2);

        given(this.postLikeRepository.findAllByPost(post, pageable)).willReturn(postLikes);

        // when
        Pagination<LikeFindResponseDto> likeFindResponseDto = this.postService.findLikeByPost("testPostId", pageable);

        // then
        assertThat(likeFindResponseDto.getResults().size()).isEqualTo(postLikes.getContent().size());
    }
}