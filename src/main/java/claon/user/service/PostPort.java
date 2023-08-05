package claon.user.service;

import claon.common.domain.Pagination;
import claon.user.domain.User;
import claon.user.dto.CenterClimbingHistoryResponseDto;
import claon.user.dto.HistoryByDateFindResponseDto;
import claon.user.dto.HistoryGroupByMonthDto;
import claon.user.dto.UserCenterResponseDto;
import claon.user.dto.UserPostDetailResponseDto;
import claon.user.dto.UserPostThumbnailResponseDto;
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
