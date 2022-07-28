package coLaon.ClaonBack.center.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterImg {
    private String url;

    public static CenterImg of(String url) {
        return new CenterImg(
                url
        );
    }
}
