package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.user.dto.CenterClimbingHistoryResponseDto;
import coLaon.ClaonBack.user.dto.PostDetailResponseDto;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.PostThumbnailResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostPort {
    Pagination<PostThumbnailResponseDto> findPostsByUser(User user, Pageable pageable);
    List<String> selectPostIdsByUserId(String userId);
    List<CenterClimbingHistoryResponseDto> findClimbingHistoryByPostIds(List<String> postIds);
    Pagination<PostDetailResponseDto> findLaonPost(User user, Pageable pageable);
}
