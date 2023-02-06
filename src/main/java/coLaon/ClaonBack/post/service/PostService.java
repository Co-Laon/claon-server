package coLaon.ClaonBack.post.service;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.repository.CenterRepository;
import coLaon.ClaonBack.center.repository.HoldInfoRepository;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import coLaon.ClaonBack.common.exception.NotFoundException;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.IdEqualValidator;
import coLaon.ClaonBack.common.validator.IsExistUrlValidator;
import coLaon.ClaonBack.common.validator.IsImageValidator;
import coLaon.ClaonBack.common.validator.IsPrivateValidator;
import coLaon.ClaonBack.post.domain.ClimbingHistory;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostContents;
import coLaon.ClaonBack.post.domain.PostReport;
import coLaon.ClaonBack.post.dto.PostContentsUrlDto;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.PostDetailResponseDto;
import coLaon.ClaonBack.post.dto.PostReportRequestDto;
import coLaon.ClaonBack.post.dto.PostReportResponseDto;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.dto.PostUpdateRequestDto;
import coLaon.ClaonBack.post.infra.PostContentsImageManager;
import coLaon.ClaonBack.post.repository.ClimbingHistoryRepository;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostReportRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.post.repository.PostRepositorySupport;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.BlockUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final BlockUserRepository blockUserRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final HoldInfoRepository holdInfoRepository;
    private final CenterRepository centerRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;
    private final PostRepositorySupport postRepositorySupport;
    private final PostReportRepository postReportRepository;
    private final PostContentsImageManager postContentsImageManager;
    private final PaginationFactory paginationFactory;

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findUserPostsByCenterAndYearMonth(
            User user,
            String nickname,
            String centerId,
            Integer year,
            Integer month,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findByNicknameAndCenterAndYearMonth(user.getId(), nickname, centerId, year, month, pageable).map(
                        post -> PostDetailResponseDto.from(
                                post,
                                post.getWriter().getNickname().equals(user.getNickname()),
                                postLikeRepository.findByLikerAndPost(user, post).isPresent(),
                                postLikeRepository.countByPost(post)))
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findPosts(
            User user,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findExceptLaonUserAndBlockUser(user.getId(), pageable).map(
                        post -> PostDetailResponseDto.from(
                                post,
                                post.getWriter().getNickname().equals(user.getNickname()),
                                postLikeRepository.findByLikerAndPost(user, post).isPresent(),
                                postLikeRepository.countByPost(post)))
        );
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDto findPost(
            User user,
            String postId
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriter().getNickname().equals(user.getNickname())) {
            IsPrivateValidator.of(post.getWriter().getNickname(), post.getWriter().getIsPrivate()).validate();

            if (!blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", post.getWriter().getNickname())
                );
            }
        }

        return PostDetailResponseDto.from(
                post,
                post.getWriter().getNickname().equals(user.getNickname()),
                postLikeRepository.findByLikerAndPost(user, post).isPresent(),
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public PostResponseDto createPost(
            User user,
            PostCreateRequestDto postCreateRequestDto
    ) {
        Center center = centerRepository.findById(postCreateRequestDto.getCenterId()).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "암장을 찾을 수 없습니다."
                )
        );

        Post post = this.postRepository.save(
                Post.of(
                        center,
                        postCreateRequestDto.getContent(),
                        postCreateRequestDto.getContentsList().stream()
                                .map(contents -> PostContents.of(
                                        contents.getUrl()
                                ))
                                .collect(Collectors.toList()),
                        user
                )
        );

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postCreateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
                                holdInfoRepository.findById(history.getHoldId()).orElseThrow(
                                        () -> new InternalServerErrorException(
                                                ErrorCode.INTERNAL_SERVER_ERROR,
                                                "홀드 정보를 찾을 수 없습니다."
                                        )),
                                history.getClimbingCount())))
                .collect(Collectors.toList());

        return PostResponseDto.from(post, climbingHistoryList);
    }

    @Transactional
    public PostResponseDto updatePost(
            User user,
            String postId,
            PostUpdateRequestDto postUpdateRequestDto
    ) {
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
                .stream().map(history ->
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

        return PostResponseDto.from(postRepository.save(post), climbingHistoryList);
    }

    @Transactional
    public PostResponseDto deletePost(User user, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriter().getId(), user.getId()).validate();

        post.delete();

        return PostResponseDto.from(this.postRepository.save(post));
    }

    @Transactional
    public PostReportResponseDto createReport(
            User user,
            String postId,
            PostReportRequestDto postReportRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        postReportRepository.findByReporterAndPost(user, post).ifPresent(
                like -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 신고한 게시글입니다."
                    );
                }
        );

        return PostReportResponseDto.from(
                postReportRepository.save(
                        PostReport.of(
                                user,
                                post,
                                postReportRequestDto.getReportType(),
                                postReportRequestDto.getContent()
                        )
                )
        );
    }

    public String uploadContents(MultipartFile image) {
        IsImageValidator.of(image).validate();

        return this.postContentsImageManager.uploadContents(image);
    }

    public void deleteContents(User user, String postId, PostContentsUrlDto postContentsUrlDto) {
        Post post = this.postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        String imagePath = postContentsUrlDto.getUrl();
        IdEqualValidator.of(post.getWriter().getId(), user.getId())
                .linkWith(IsExistUrlValidator.of(post.getContentList(), imagePath)).validate();

        this.postContentsImageManager.deleteContents(imagePath);
    }
}