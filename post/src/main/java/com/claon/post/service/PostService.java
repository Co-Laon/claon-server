package com.claon.post.service;

import com.claon.post.common.domain.Pagination;
import com.claon.post.common.domain.PaginationFactory;
import com.claon.post.common.domain.RequestUserInfo;
import com.claon.post.common.exception.*;
import com.claon.post.common.validator.IdEqualValidator;
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
    private final BlockUserRepository blockUserRepository;

    private final PaginationFactory paginationFactory;

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findUserPostsByCenterAndYearMonth(
            RequestUserInfo userInfo,
            String centerId,
            Integer year,
            Integer month,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findByCenterAndYearMonth(userInfo.id(), centerId, year, month, pageable).map(
                        post -> PostDetailResponseDto.from(
                                post,
                                post.getWriterId().equals(userInfo.id()),
                                postLikeRepository.findByLikerIdAndPost(userInfo.id(), post).isPresent(),
                                postLikeRepository.countByPost(post)))
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostDetailResponseDto> findPosts(
            RequestUserInfo userInfo,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findExceptBlockUser(userInfo.id(), pageable).map(
                        post -> PostDetailResponseDto.from(
                                post,
                                post.getWriterId().equals(userInfo.id()),
                                postLikeRepository.findByLikerIdAndPost(userInfo.id(), post).isPresent(),
                                postLikeRepository.countByPost(post)))
        );
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDto findPost(
            RequestUserInfo userInfo,
            String postId
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        if (!post.getWriterId().equals(userInfo.id())) {
            if (!blockUserRepository.findBlock(userInfo.id(), post.getWriterId()).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", post.getWriterId())
                );
            }
        }

        return PostDetailResponseDto.from(
                post,
                post.getWriterId().equals(userInfo.id()),
                postLikeRepository.findByLikerIdAndPost(userInfo.id(), post).isPresent(),
                postLikeRepository.countByPost(post)
        );
    }

    @Transactional
    public PostResponseDto createPost(
            RequestUserInfo userInfo,
            PostCreateRequestDto postCreateRequestDto
    ) {
        Post post = this.postRepository.save(
                Post.of(
                        postCreateRequestDto.getCenterId(),
                        postCreateRequestDto.getContent(),
                        postCreateRequestDto.getContentsList().stream()
                                .map(contents -> PostContents.of(
                                        contents.getUrl()
                                ))
                                .collect(Collectors.toList()),
                        userInfo.id()
                )
        );

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postCreateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
                                history.getHoldId(),
                                history.getClimbingCount())))
                .collect(Collectors.toList());

        return PostResponseDto.from(post, climbingHistoryList);
    }

    @Transactional
    public PostResponseDto updatePost(
            RequestUserInfo userInfo,
            String postId,
            PostUpdateRequestDto postUpdateRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriterId(), userInfo.id()).validate();

        climbingHistoryRepository.deleteAllByPost(postId);

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postUpdateRequestDto.getClimbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history ->
                        climbingHistoryRepository.save(ClimbingHistory.of(
                                post,
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
    public PostResponseDto deletePost(RequestUserInfo userInfo, String postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        IdEqualValidator.of(post.getWriterId(), userInfo.id()).validate();

        post.delete();

        return PostResponseDto.from(this.postRepository.save(post));
    }

    @Transactional
    public PostReportResponseDto createReport(
            RequestUserInfo userInfo,
            String postId,
            PostReportRequestDto postReportRequestDto
    ) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );

        postReportRepository.findByReporterIdAndPost(userInfo.id(), post).ifPresent(
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
                                userInfo.id(),
                                post,
                                postReportRequestDto.getReportType(),
                                postReportRequestDto.getContent()
                        )
                )
        );
    }

    @Transactional
    public Pagination<PostThumbnailResponseDto> findPostThumbnailsByUser(RequestUserInfo userInfo, Pageable pageable) {
        return this.paginationFactory.create(
                postRepository.findByWriterAndIsDeletedFalse(userInfo.id(), pageable)
                        .map(post -> PostThumbnailResponseDto.from(
                                post.getId(),
                                post.getThumbnailUrl(),
                                post.getClimbingHistoryList().stream()
                                        .map(history -> ClimbingHistoryResponseDto.from(
                                                history.getHoldInfoId(),
                                                history.getClimbingCount()
                                        ))
                                        .collect(Collectors.toList())
                        ))
        );
    }
}