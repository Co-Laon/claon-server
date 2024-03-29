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
import com.claon.post.dto.request.PostCreateRequestDto;
import com.claon.post.dto.request.PostReportRequestDto;
import com.claon.post.dto.request.PostUpdateRequestDto;
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
                postRepositorySupport.findByCenterAndYearMonth(userInfo.id(), centerId, year, month, pageable)
                        .map(post -> PostDetailResponseDto.from(
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
                postRepositorySupport.findExceptBlockUser(userInfo.id(), pageable)
                        .map(post -> PostDetailResponseDto.from(
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
        Post post = findPostById(postId);

        validateBlockedUser(userInfo, post.getWriterId());

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
                        postCreateRequestDto.centerId(),
                        postCreateRequestDto.content(),
                        postCreateRequestDto.contentsList()
                                .stream().map(contents -> PostContents.of(
                                        contents.url()))
                                .collect(Collectors.toList()),
                        userInfo.id()
                )
        );

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postCreateRequestDto.climbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history -> climbingHistoryRepository.save(ClimbingHistory.of(
                        post,
                        history.holdId(),
                        history.climbingCount())))
                .collect(Collectors.toList());

        return PostResponseDto.from(post, climbingHistoryList);
    }

    @Transactional
    public PostResponseDto updatePost(
            RequestUserInfo userInfo,
            String postId,
            PostUpdateRequestDto postUpdateRequestDto
    ) {
        Post post = findPostById(postId);

        IdEqualValidator.of(post.getWriterId(), userInfo.id()).validate();

        climbingHistoryRepository.deleteAllByPost(postId);

        List<ClimbingHistory> climbingHistoryList = Optional.ofNullable(postUpdateRequestDto.climbingHistories())
                .orElse(Collections.emptyList())
                .stream().map(history -> climbingHistoryRepository.save(ClimbingHistory.of(
                        post,
                        history.holdId(),
                        history.climbingCount())))
                .collect(Collectors.toList());

        post.update(
                postUpdateRequestDto.content(),
                postUpdateRequestDto.contentsList().stream()
                        .map(contents -> PostContents.of(
                                contents.url()
                        ))
                        .collect(Collectors.toList())
        );

        return PostResponseDto.from(postRepository.save(post), climbingHistoryList);
    }

    @Transactional
    public PostResponseDto deletePost(RequestUserInfo userInfo, String postId) {
        Post post = findPostById(postId);

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
        Post post = findPostById(postId);

        postReportRepository.findByReporterIdAndPost(userInfo.id(), post).ifPresent(
                report -> {
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
                                postReportRequestDto.reportType(),
                                postReportRequestDto.content()
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostThumbnailResponseDto> findPostThumbnailsByUser(RequestUserInfo userInfo, Pageable pageable) {
        return this.paginationFactory.create(
                postRepository.findByWriterAndIsDeletedFalse(userInfo.id(), pageable)
                        .map(post -> PostThumbnailResponseDto.from(
                                post.getId(),
                                post.getThumbnailUrl(),
                                post.getClimbingHistoryList().stream()
                                        .map(history -> ClimbingHistoryResponseDto.from(
                                                history.getHoldInfoId(),
                                                history.getClimbingCount()))
                                        .collect(Collectors.toList())))
        );
    }

    @Transactional(readOnly = true)
    public Pagination<PostThumbnailResponseDto> findCenterPostThumbnailsByUser(
            RequestUserInfo userInfo,
            String centerId,
            Optional<String> holdId,
            Pageable pageable
    ) {
        return this.paginationFactory.create(
                postRepositorySupport.findByCenterAndHoldExceptBlockUser(centerId, holdId, userInfo.id(), pageable)
                        .map(post -> PostThumbnailResponseDto.from(
                                post.getId(),
                                post.getThumbnailUrl()))
        );
    }

    @Transactional(readOnly = true)
    public Long countPostByCenter(RequestUserInfo userInfo, String centerId) {
        return postRepositorySupport.countByCenter(userInfo.id(), centerId);
    }

    private Post findPostById(String postId) {
        return postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(
                () -> new NotFoundException(
                        ErrorCode.DATA_DOES_NOT_EXIST,
                        "게시글을 찾을 수 없습니다."
                )
        );
    }

    private void validateBlockedUser(RequestUserInfo userInfo, String writerId) {
        if (!userInfo.id().equals(writerId)) {
            if (!blockUserRepository.findBlock(userInfo.id(), writerId).isEmpty()) {
                throw new UnauthorizedException(
                        ErrorCode.NOT_ACCESSIBLE,
                        String.format("%s을 찾을 수 없습니다.", writerId)
                );
            }
        }
    }
}