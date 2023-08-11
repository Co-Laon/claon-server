package com.claon.center.service;

import com.claon.center.dto.CenterPostThumbnailResponseDto;
import com.claon.center.common.domain.Pagination;
import org.springframework.data.domain.Pageable;

public interface PostPort {
    Pagination<CenterPostThumbnailResponseDto> findByCenterAndHoldExceptBlockUser(String centerId, String holdId, String userId, Pageable pageable);

    Pagination<CenterPostThumbnailResponseDto> findByCenterExceptBlockUser(String centerId, String userId, Pageable pageable);

    Integer countByCenterExceptBlockUser(String centerId, String userId);
}
