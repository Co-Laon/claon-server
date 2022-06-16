package coLaon.ClaonBack.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LaonPage {

    public int pageNum;
    public int LastPageNum;

    public static LaonPage of(
            int pageNum,
            int LastPageNum
    ) {
        return new LaonPage(
                pageNum,
                LastPageNum
        );
    }

}
