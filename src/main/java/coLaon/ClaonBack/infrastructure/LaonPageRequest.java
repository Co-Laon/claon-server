package coLaon.ClaonBack.infrastructure;

import org.springframework.data.domain.PageRequest;


public class LaonPageRequest {

    private int pageNum = 1;
    private int pageSize = 10; // limit

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum <= 0 ? 1 : pageNum;
    }

    public void setPageSize(int pageSize) {
        int defaultPageSize = 10;
        int maxPageSize = 20;
        this.pageSize = pageSize > maxPageSize ? defaultPageSize : pageSize;
    }

    public PageRequest of() {
        return PageRequest.of(this.pageNum - 1, pageSize);
    }
}
