package coLaon.ClaonBack.user.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.dto.PostThumbnailResponseDto;
import org.springframework.data.domain.Pageable;

public interface PostPort {
    Pagination<PostThumbnailResponseDto> findPostsByUser(User user, Pageable pageable);
}
