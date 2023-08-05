package claon.center.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
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
