package com.claon.user.service;

import com.claon.user.common.domain.Pagination;
import com.claon.user.domain.User;
import com.claon.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostPort {
    Pagination<UserPostThumbnailResponseDto> findPostsByUser(User user, Pageable pageable);
    List<String> selectPostIdsByUserId(String userId);
    List<CenterClimbingHistoryResponseDto> findClimbingHistoryByPostIds(List<String> postIds);
    Pagination<UserPostDetailResponseDto> findLaonPost(User user, Pageable pageable);
    List<HistoryGroupByMonthDto> findByCenterIdAndUserId(String centerId, String userId);
    Page<UserCenterResponseDto> selectDistinctCenterByUser(User user, Pageable pageable);
    List<HistoryByDateFindResponseDto> findHistoryByDate(String userId, Integer year, Integer month);
}
