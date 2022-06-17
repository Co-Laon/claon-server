package coLaon.ClaonBack.common.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LaonPage {

    public int pageNum;
    public int lastPageNum;

    public static LaonPage of(
            int pageNum,
            int lastPageNum
    ) {
        return new LaonPage(
                pageNum,
                lastPageNum
        );
    }
}
