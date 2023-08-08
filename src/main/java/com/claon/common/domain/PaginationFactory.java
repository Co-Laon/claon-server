package com.claon.common.domain;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationFactory {
    public <T> Pagination<T> create(Page<T> p) {
        return new Pagination<>(
                this.buildNextPageNum(p),
                this.buildPreviousPageNum(p),
                p.getTotalElements(),
                p.getContent()
        );
    }

    private <T> Integer buildNextPageNum(Page<T> p) {
        if (p.getTotalPages() - 1 < p.getPageable().getPageNumber() + 1) return -1;

        return Math.min(p.getPageable().getPageNumber() + 1, Math.max(p.getTotalPages() - 1, 0));
    }

    private <T> Integer buildPreviousPageNum(Page<T> p) {
        if (p.getPageable().getPageNumber() - 1 < 0) return -1;

        return Math.max(0, p.getPageable().getPageNumber() - 1);
    }
}
