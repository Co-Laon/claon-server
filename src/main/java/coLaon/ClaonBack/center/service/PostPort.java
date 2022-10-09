package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.dto.PostThumbnailResponseDto;
import coLaon.ClaonBack.common.domain.Pagination;
import org.springframework.data.domain.Pageable;

public interface PostPort {
    Pagination<PostThumbnailResponseDto> findByCenterAndHoldExceptBlockUser(String centerId, String holdId, String userId, Pageable pageable);

    Pagination<PostThumbnailResponseDto> findByCenterExceptBlockUser(String centerId, String userId, Pageable pageable);

    Integer countByCenterExceptBlockUser(String centerId, String userId);
}
