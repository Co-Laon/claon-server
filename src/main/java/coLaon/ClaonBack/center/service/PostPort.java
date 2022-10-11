package coLaon.ClaonBack.center.service;

import coLaon.ClaonBack.center.dto.CenterPostThumbnailResponseDto;
import coLaon.ClaonBack.common.domain.Pagination;
import org.springframework.data.domain.Pageable;

public interface PostPort {
    Pagination<CenterPostThumbnailResponseDto> findByCenterAndHoldExceptBlockUser(String centerId, String holdId, String userId, Pageable pageable);

    Pagination<CenterPostThumbnailResponseDto> findByCenterExceptBlockUser(String centerId, String userId, Pageable pageable);

    Integer countByCenterExceptBlockUser(String centerId, String userId);
}
