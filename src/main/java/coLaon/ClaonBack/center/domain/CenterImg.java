package coLaon.ClaonBack.center.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Getter
public class CenterImg {
    private final String url;

    private CenterImg(String url) {
        this.url = url;
    }

    public static CenterImg of(String url) {
        return new CenterImg(
                url
        );
    }
}
