package coLaon.ClaonBack.common.domain;

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
        return Math.min(p.getPageable().getPageNumber() + 1, (int) Math.max(p.getTotalElements() - 1, 0));
    }

    private <T> Integer buildPreviousPageNum(Page<T> p) {
        return Math.max(0, p.getPageable().getPageNumber() - 1);
    }
}
