package coLaon.ClaonBack;

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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
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
        //given
        PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(
                "center1",
                "hold",
                "testContent",
                List.of(new PostContentsDto("test.com/test.png"))
        );

        given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
        mockedPost.when(() -> Post.of(
                postCreateRequestDto.getCenterName(),
                postCreateRequestDto.getHoldInfo(),
                postCreateRequestDto.getContent(),
                user)).thenReturn(post);
        given(this.postRepository.save(post)).willReturn(post);

        mockedPostContents.when(() -> PostContents.of(
                post.getId(),
                post,
                "test.com/test.png"
        )).thenReturn(postContents);
        given(this.postContentsRepository.save(postContents)).willReturn(postContents);

        //when
        PostResponseDto postResponseDto = this.postService.createPost("testUserId", postCreateRequestDto);

        //then
        assertThat(postResponseDto).isNotNull();
        assertThat(postCreateRequestDto.getCenterName()).isEqualTo(postResponseDto.getCenterName());
        assertThat(postCreateRequestDto.getContentsList().size()).isEqualTo(1);
        mockedPost.close();
        mockedPostContents.close();
    }

    @Test
    @DisplayName("Failed case (Invalid Image Format in post) for create post")
    void failedCreatePost_InvalidImageFormat() {
        try (MockedStatic<Post> mockedPost = mockStatic(Post.class)) {
            //given
            List<PostContentsDto> postContentsDtoList = new ArrayList<>(Arrays.asList("png", "jpg", "gif"))
                    .stream()
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
            mockedPost.when(() -> Post.of(
                    postCreateRequestDto.getCenterName(),
                    postCreateRequestDto.getHoldInfo(),
                    postCreateRequestDto.getContent(),
                    user)).thenReturn(post);
            given(this.postRepository.save(post)).willReturn(post);

            //when
            final BadRequestException ex = Assertions.assertThrows(
                    BadRequestException.class,
                    () -> postService.createPost("testUserId", postCreateRequestDto)
            );

            //then
            assertThat(ex.getMessage()).isEqualTo("이미지 형식이 잘못되었습니다.");
        }
    }

    @Test
    @DisplayName("Failed case (Invalid Image number in post) for create post")
    void failedCreatePost_InvalidImageNumber() {
        try (MockedStatic<Post> mockedPost = mockStatic(Post.class)) {
            //given
            List<PostContentsDto> postContentsDtoList = Stream.iterate(0, i -> i + 1).limit(11)
                    .map(s -> "test.com/test" + s + ".png")
                    .map(PostContentsDto::new)
                    .collect(Collectors.toList());

            PostCreateRequestDto postCreateRequestDto = new PostCreateRequestDto(
                    "center1",
                    "hold",
                    "testContent",
                    postContentsDtoList
            );

            given(this.userRepository.findById("testUserId")).willReturn(Optional.of(user));
            mockedPost.when(() -> Post.of(
                    postCreateRequestDto.getCenterName(),
                    postCreateRequestDto.getHoldInfo(),
                    postCreateRequestDto.getContent(),
                    user)).thenReturn(post);
            given(this.postRepository.save(post)).willReturn(post);

            //when
            final BadRequestException ex = Assertions.assertThrows(
                    BadRequestException.class,
                    () -> postService.createPost("testUserId", postCreateRequestDto)
            );

            //then
            assertThat(ex.getMessage()).isEqualTo("이미지 혹은 영상은 1개 이상 10개 이하 업로드해야 합니다.");
        }
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
        //given
        given(this.postRepository.findById("testPostId")).willReturn(Optional.of(post));

        ArrayList<PostLike> postLikes = new ArrayList<>(Arrays.asList(postLike, postLike2));

        given(this.postLikeRepository.findAllByPostOrderByCreatedAt(post)).willReturn(postLikes);

        //when
        List<LikeFindResponseDto> likeFindResponseDto = this.postService.findLikeByPost("testPostId");

        //then
        assertThat(likeFindResponseDto.size()).isEqualTo(postLikes.size());
    }
}