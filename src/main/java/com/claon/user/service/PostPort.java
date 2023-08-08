package com.claon.user.service;

import com.claon.common.domain.Pagination;
import com.claon.user.domain.User;
import com.claon.user.dto.CenterClimbingHistoryResponseDto;
import com.claon.user.dto.HistoryByDateFindResponseDto;
import com.claon.user.dto.HistoryGroupByMonthDto;
import com.claon.user.dto.UserCenterResponseDto;
import com.claon.user.dto.UserPostDetailResponseDto;
import com.claon.user.dto.UserPostThumbnailResponseDto;
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
