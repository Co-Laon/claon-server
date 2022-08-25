package coLaon.ClaonBack.post.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.ContentsImageFormatValidator;
import coLaon.ClaonBack.common.validator.IdEqualValidator;
import coLaon.ClaonBack.common.validator.IsHoldValidator;
import coLaon.ClaonBack.common.validator.IsPrivateValidator;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.PostDetailResponseDto;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.dto.PostThumbnailResponseDto;
import coLaon.ClaonBack.post.dto.PostUpdateRequestDto;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.post.repository.PostRepositorySupport;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final BlockUserRepository blockUserRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final HoldInfoRepository holdInfoRepository;
    private final CenterRepository centerRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;
    private final PostRepositorySupport postRepositorySupport;
    private final PaginationFactory paginationFactory;

    @Transactional(readOnly = true)
    public PostDetailResponseDto findPost(String userId, String postId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).size() > 0) {
            throw new UnauthorizedException(ErrorCode.NOT_ACCESSIBLE, "조회가 불가능한 이용자입니다.");
        }

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
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        ContentsImageFormatValidator.of(postCreateRequestDto.getContentsList()).validate();

        Post post = this.postRepository.save(
                Post.of(
                        center,
                        postCreateRequestDto.getContent(),
                        postCreateRequestDto.getContentsList().stream()
                                .map(contents -> PostContents.of(
                                        contents.getUrl()
                                ))
                                .collect(Collectors.toList()),
                        writer
                )
        );

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postCreateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream()
                .map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
                                holdInfoRepository.findById(history.getHoldId()).orElseThrow(
                                        () -> new InternalServerErrorException(
                                                ErrorCode.INTERNAL_SERVER_ERROR,
                                                "홀드 정보를 찾을 수 없습니다."
                                        )),
                                history.getClimbingCount())))
                .collect(Collectors.toList());

        return PostResponseDto.from(
                post,
                climbingHistoryList
                        .stream()
                        .map(ClimbingHistory::getHoldInfo)
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public PostResponseDto updatePost(
            String userId,
            String postId,
            PostUpdateRequestDto postUpdateRequestDto
    ) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriter().getId(), user.getId()).validate();

        climbingHistoryRepository.deleteAllByPost(postId);
        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postUpdateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream()
                .map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
                                holdInfoRepository.findById(history.getHoldId()).orElseThrow(
                                        () -> new InternalServerErrorException(
                                                ErrorCode.INTERNAL_SERVER_ERROR,
                                                "홀드 정보를 찾을 수 없습니다."
                                        )),
                                history.getClimbingCount())))
                .collect(Collectors.toList());

        post.update(
                postUpdateRequestDto.getContent(),
                postUpdateRequestDto.getContentsList().stream()
                        .map(contents -> PostContents.of(
                                contents.getUrl()
                        ))
                        .collect(Collectors.toList())
        );

        return PostResponseDto.from(
                postRepository.save(post),
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
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriter().getId(), writer.getId()).validate();

        post.delete();

        return PostResponseDto.from(
                this.postRepository.save(post)
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostThumbnailResponseDto> getUserPosts(
            String loggedInUserId,
            String targetUserNickname,
            Pageable pageable
    ) {
        userRepository.findById(loggedInUserId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        User targetUser = userRepository.findByNickname(targetUserNickname).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        String.format("%s을 찾을 수 없습니다.", targetUserNickname)
                )
        );

        // individual user page
        if (!loggedInUserId.equals(targetUser.getId())) {
            IsPrivateValidator.of(targetUser.getIsPrivate()).validate();

            if (blockUserRepository.findBlock(targetUser.getId(), loggedInUserId).size() > 0) {
                throw new UnauthorizedException(ErrorCode.NOT_ACCESSIBLE, "조회가 불가능한 이용자입니다.");
            }
        }

        return this.paginationFactory.create(
                postRepository.findByWriterAndIsDeletedFalse(targetUser, pageable).map(PostThumbnailResponseDto::from)
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostThumbnailResponseDto> getCenterPosts(
            String userId,
            String centerId,
            Optional<String> holdId,
            Pageable pageable
    ) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Center center = centerRepository.findById(centerId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장 정보를 찾을 수 없습니다."
                )
        );

        if (holdId.isPresent()) {
            List<String> allHoldsByCenter = holdInfoRepository.findAllByCenter(center)
                    .stream().map(BaseEntity::getId).collect(Collectors.toList());

            IsHoldValidator.of(holdId.get(), allHoldsByCenter).validate();

            return this.paginationFactory.create(
                    postRepositorySupport.findByCenterAndHoldExceptBlockUser(center.getId(), holdId.get(), user.getId(), pageable)
                            .map(PostThumbnailResponseDto::from)
            );
        }

        return this.paginationFactory.create(
                postRepositorySupport.findByCenterExceptBlockUser(center.getId(), user.getId(), pageable)
                        .map(PostThumbnailResponseDto::from)
        );
    }
}