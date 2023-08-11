package com.claon.center.repository;

import com.claon.center.domain.Center;
import com.claon.center.domain.enums.CenterSearchOption;
import com.claon.center.dto.CenterPreviewResponseDto;
import com.claon.center.dto.QCenterPreviewResponseDto;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.claon.center.domain.QCenter.center;
import static com.claon.center.domain.QCenterBookmark.centerBookmark;
import static com.claon.center.domain.QCenterReview.centerReview;
import static com.claon.center.domain.QSectorInfo.sectorInfo;

@Repository
public class CenterRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public CenterRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(Center.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<CenterPreviewResponseDto> searchCenter(String name, Pageable pageable) {
        JPQLQuery<CenterPreviewResponseDto> query = jpaQueryFactory
                .select(new QCenterPreviewResponseDto(center.id, center.name, center.imgList, centerReview.rank.avg()))
                .from(centerReview)
                .rightJoin(centerReview.center, center)
                .where(center.name.containsIgnoreCase(name))
                .groupBy(center.id);

        long totalCount = query.fetchCount();
        List<CenterPreviewResponseDto> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    public Page<CenterPreviewResponseDto> findCenterByOption(String userId, CenterSearchOption option, Pageable pageable) {
        JPQLQuery<CenterPreviewResponseDto> query = jpaQueryFactory
                .select(new QCenterPreviewResponseDto(center.id, center.name, center.imgList, centerReview.rank.avg()))
                .from(centerReview)
                .rightJoin(centerReview.center, center)
                .groupBy(center.id);

        query = switch (option) {
            case BOOKMARK -> findBookMarkedCenters(query, userId);
            case NEW_SETTING -> findNewSettingCenters(query);
            case NEWLY_REGISTERED -> findNewlyRegisteredCenters(query);
        };

        long totalCount = query.fetchCount();
        List<CenterPreviewResponseDto> results = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query).fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    private JPQLQuery<CenterPreviewResponseDto> findNewSettingCenters(JPQLQuery<CenterPreviewResponseDto> query) {
        LocalDate standardPlus = LocalDate.now().plusDays(2);
        LocalDate standardMinus = LocalDate.now().minusDays(2);

        return query
                .join(sectorInfo).on(sectorInfo.center.id.eq(center.id))
                .where(sectorInfo.end.isNotNull()
                        .and(sectorInfo.end.between(standardMinus, standardPlus)));
    }

    private JPQLQuery<CenterPreviewResponseDto> findBookMarkedCenters(JPQLQuery<CenterPreviewResponseDto> query, String userId) {
        return query
                .join(centerBookmark).on(centerBookmark.center.id.eq(center.id))
                .where(centerBookmark.userId.eq(userId));
    }

    private JPQLQuery<CenterPreviewResponseDto> findNewlyRegisteredCenters(JPQLQuery<CenterPreviewResponseDto> query) {
        LocalDateTime standardDate = LocalDate.now().atStartOfDay().minusMonths(1);
        return query
                .where(center.createdAt.after(standardDate));
    }
}
