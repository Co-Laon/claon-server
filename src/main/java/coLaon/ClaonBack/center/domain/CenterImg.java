package coLaon.ClaonBack.center.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CenterImg {
    private final String url;

    public static CenterImg of(String url) {
        return new CenterImg(
                url
        );
    }
}
