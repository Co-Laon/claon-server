package coLaon.ClaonBack.post.service;

import coLaon.ClaonBack.center.dto.PostThumbnailResponseDto;
import coLaon.ClaonBack.center.service.PostPort;
import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.post.repository.PostRepositorySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostToCenterAdapter implements PostPort {
    private final PostRepositorySupport postRepositorySupport;
    private final PaginationFactory paginationFactory;

    @Override
    public Pagination<PostThumbnailResponseDto> findByCenterAndHoldExceptBlockUser(String centerId, String holdId, String userId, Pageable pageable) {
        return this.paginationFactory.create(
                postRepositorySupport.findByCenterAndHoldExceptBlockUser(centerId, holdId, userId, pageable)
                            .map((post) -> PostThumbnailResponseDto.from(post.getId(), post.getThumbnailUrl()))
            );
    }

    @Override
    public Pagination<PostThumbnailResponseDto> findByCenterExceptBlockUser(String centerId, String userId, Pageable pageable) {
        return this.paginationFactory.create(
                postRepositorySupport.findByCenterExceptBlockUser(centerId, userId, pageable)
                        .map((post) -> PostThumbnailResponseDto.from(post.getId(), post.getThumbnailUrl()))
        );
    }

    @Override
    public Integer countByCenterExceptBlockUser(String centerId, String userId) {
        return postRepositorySupport.countByCenterExceptBlockUser(centerId, userId);
    }
}
