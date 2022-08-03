package coLaon.ClaonBack.post.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.ContentsImageFormatValidator;
import coLaon.ClaonBack.common.validator.IdEqualValidator;
import coLaon.ClaonBack.common.validator.IsPrivateValidator;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeFindResponseDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.PostDetailResponseDto;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.dto.PostThumbnailResponseDto;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostContentsRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final BlockUserRepository blockUserRepository;
    private final PostRepository postRepository;
    private final PostContentsRepository postContentsRepository;
    private final PostLikeRepository postLikeRepository;
    private final HoldInfoRepository holdInfoRepository;
    private final CenterRepository centerRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;
    private final PaginationFactory paginationFactory;

    @Transactional(readOnly = true)
    public PostDetailResponseDto findPost(String userId, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).ifPresent(
                b -> {
                    throw new BadRequestException(
                            ErrorCode.NOT_ACCESSIBLE,
                            "차단 관계입니다."
                    );
                }
        );

        IsPrivateValidator.of(post.getWriter().getIsPrivate()).validate();

        return PostDetailResponseDto.from(
                post,
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public PostResponseDto createPost(String userId, PostCreateRequestDto postCreateRequestDto) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = centerRepository.findById(postCreateRequestDto.getCenterId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        ContentsImageFormatValidator.of(postCreateRequestDto.getContentsList()).validate();

        Post post = this.postRepository.save(
                Post.of(
                        center,
                        postCreateRequestDto.getContent(),
                        writer
                )
        );

        List<ClimbingHistory> climbingHistoryList = postCreateRequestDto.getClimbingHistories()
                .stream()
                .map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
                                holdInfoRepository.findById(history.getHoldId()).orElseThrow(
                                        () -> new BadRequestException(
                                                ErrorCode.ROW_DOES_NOT_EXIST,
                                                "홀드 정보를 찾을 수 없습니다."
                                        )),
                                history.getClimbingCount())))
                .collect(Collectors.toList());

        List<PostContents> postContentsList = postCreateRequestDto.getContentsList()
                .stream()
                .map(dto -> PostContents.of(
                        post,
                        dto.getUrl()))
                .collect(Collectors.toList());

        return PostResponseDto.from(
                post,
                postContentsList
                        .stream()
                        .map(postContentsRepository::save)
                        .map(PostContents::getUrl)
                        .collect(Collectors.toList()),
                climbingHistoryList
                        .stream()
                        .map(ClimbingHistory::getHoldInfo)
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public PostResponseDto deletePost(String postId, String userId) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriter().getId(), writer.getId()).validate();

        post.delete();

        return PostResponseDto.from(
                this.postRepository.save(post)
        );
    }

    @Transactional
    public LikeResponseDto createLike(String userId, String postId) {
        User liker = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        postLikeRepository.findByLikerAndPost(liker, post).ifPresent(
                like -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 좋아요 한 게시글입니다."
                    );
                }
        );

        return LikeResponseDto.from(
                postLikeRepository.save(PostLike.of(liker, post)),
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public LikeResponseDto deleteLike(String userId, String postId) {
        User liker = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        PostLike like = postLikeRepository.findByLikerAndPost(liker, post).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "좋아요 하지 않은 게시글입니다."
                )
        );

        postLikeRepository.delete(like);

        return LikeResponseDto.from(
                like,
                postLikeRepository.countByPost(like.getPost())
        );
    }

    @Transactional(readOnly = true)
    public Pagination<LikeFindResponseDto> findLikeByPost(String userId, String postId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        return this.paginationFactory.create(
                postLikeRepository.findAllByPost(post, pageable)
                        .map(LikeFindResponseDto::from)
        );
    }

    @Transactional(readOnly = true)
    public Page<PostThumbnailResponseDto> getUserPosts(String loginedUserId, String targetUserNickname, Pageable pageable){
        User targetUser = userRepository.findByNickname(targetUserNickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "요청한 사용자가 존재하지 않습니다. "
                )
        );

        // my page
        if (loginedUserId.equals(targetUser.getId())) {
            postRepository.findByWriterOrderByCreatedAtDesc(targetUser, pageable).map(PostThumbnailResponseDto::from);
        }

        if (targetUser.getIsPrivate()){
            throw new UnauthorizedException(ErrorCode.NOT_ACCESSIBLE, "해당 사용자는 비공개 입니다. ");
        }

        blockUserRepository.findByUserIdAndBlockId(targetUser.getId(), loginedUserId).ifPresent(
                (blockUser -> {
                    throw new UnauthorizedException(ErrorCode.NOT_ACCESSIBLE, "조회가 불가능한 사용자입니다. ");
                })
        );

        return postRepository.findByWriterOrderByCreatedAtDesc(targetUser, pageable).map(PostThumbnailResponseDto::from);
    }
}