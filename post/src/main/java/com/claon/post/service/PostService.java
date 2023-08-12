package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.exception.*;
import com.claon.post.common.validator.IdEqualValidator;
import com.claon.post.common.validator.IsExistUrlValidator;
import com.claon.post.common.validator.IsPrivateValidator;
import com.claon.post.domain.ClimbingHistory;
import com.claon.post.domain.Post;
import com.claon.post.domain.PostContents;
import com.claon.post.domain.PostReport;
import com.claon.post.dto.*;
import com.claon.post.repository.*;
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
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final ClimbingHistoryRepository climbingHistoryRepository;
    private final PostRepositorySupport postRepositorySupport;
    private final PostReportRepository postReportRepository;
    private final PaginationFactory paginationFactory;

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findUserPostsByCenterAndYearMonth(
            String userId,
            String nickname,
            String centerId,
            Integer year,
            Integer month,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findByNicknameAndCenterAndYearMonth(userId, nickname, centerId, year, month, pageable).map(
                        post -> PostDetailResponseDto.from(
                                post,
                                post.getWriterId().equals(userId),
                                postLikeRepository.findByLikerIdAndPost(userId, post).isPresent(),
                                postLikeRepository.countByPost(post)))
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findPosts(
            String userId,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findExceptLaonUserAndBlockUser(userId, pageable).map(
                        post -> PostDetailResponseDto.from(
                                post,
                                post.getWriterId().equals(userId),
                                postLikeRepository.findByLikerIdAndPost(userId, post).isPresent(),
                                postLikeRepository.countByPost(post)))
        );
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDto findPost(
            String userId,
            String postId
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriterId().equals(userId)) {
//            IsPrivateValidator.of(post.getWriter().getNickname(), post.getWriter().getIsPrivate()).validate();
//
//            if (!blockUserRepository.findBlock(user.getId(), post.getWriter().getId()).isEmpty()) {
//                throw new UnauthorizedException(
//                        ErrorCode.NOT_ACCESSIBLE,
//                        String.format("%s을 찾을 수 없습니다.", post.getWriter().getNickname())
//                );
//            }
        }

        return PostDetailResponseDto.from(
                post,
                post.getWriterId().equals(userId),
                postLikeRepository.findByLikerIdAndPost(userId, post).isPresent(),
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public PostResponseDto createPost(
            String userId,
            PostCreateRequestDto postCreateRequestDto
    ) {
//        Center center = centerRepository.findById(postCreateRequestDto.getCenterId()).orElseThrow(
//                () -> new NotFoundException(
//                        ErrorCode.DATA_DOES_NOT_EXIST,
//                        "암장을 찾을 수 없습니다."
//                )
//        );

        Post post = this.postRepository.save(
                Post.of(
                        postCreateRequestDto.getCenterId(),
                        postCreateRequestDto.getContent(),
                        postCreateRequestDto.getContentsList().stream()
                                .map(contents -> PostContents.of(
                                        contents.getUrl()
                                ))
                                .collect(Collectors.toList()),
                        userId
                )
        );

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postCreateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
//                                holdInfoRepository.findById(history.getHoldId()).orElseThrow(
//                                        () -> new InternalServerErrorException(
//                                                ErrorCode.INTERNAL_SERVER_ERROR,
//                                                "홀드 정보를 찾을 수 없습니다."
//                                        )),
                                history.getHoldId(),
                                history.getClimbingCount())))
                .collect(Collectors.toList());

        return PostResponseDto.from(post, climbingHistoryList);
    }

    @Transactional
    public PostResponseDto updatePost(
            String userId,
            String postId,
            PostUpdateRequestDto postUpdateRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriterId(), userId).validate();

        climbingHistoryRepository.deleteAllByPost(postId);

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postUpdateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
//                                holdInfoRepository.findById(history.getHoldId()).orElseThrow(
//                                        () -> new InternalServerErrorException(
//                                                ErrorCode.INTERNAL_SERVER_ERROR,
//                                                "홀드 정보를 찾을 수 없습니다."
//                                        )),
                                history.getHoldId(),
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
    public PostResponseDto deletePost(String userId, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriterId(), userId).validate();

        post.delete();

        return PostResponseDto.from(this.postRepository.save(post));
    }

    @Transactional
    public PostReportResponseDto createReport(
            String userId,
            String postId,
            PostReportRequestDto postReportRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        postReportRepository.findByReporterIdAndPost(userId, post).ifPresent(
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
                                userId,
                                post,
                                postReportRequestDto.getReportType(),
                                postReportRequestDto.getContent()
                        )
                )
        );
    }
}