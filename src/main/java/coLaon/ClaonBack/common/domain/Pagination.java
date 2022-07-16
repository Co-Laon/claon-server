package coLaon.ClaonBack.common.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class Pagination<T> {
    private Integer nextPageNum;
    private Integer previousPageNum;
    private Long totalCount;
    private List<T> results;

    public Pagination(
            Integer nextPageNum,
            Integer previousPageNum,
            Long totalCount,
            List<T> results
    ) {
        this.nextPageNum = nextPageNum;
        this.previousPageNum = previousPageNum;
        this.totalCount = totalCount;
        this.results = results;
    }
}
