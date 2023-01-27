package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.CenterClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.HistoryByDateFindResponseDto;
import coLaon.ClaonBack.user.dto.HistoryGroupByMonthDto;
import coLaon.ClaonBack.user.dto.UserCenterResponseDto;
import coLaon.ClaonBack.user.dto.UserPostDetailResponseDto;
import coLaon.ClaonBack.user.dto.UserPostThumbnailResponseDto;
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
