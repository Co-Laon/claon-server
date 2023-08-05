package claon.post.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PostContents {
    private String url;

    public static PostContents of(
            String url
    ) {
        return new PostContents(
                url
        );
    }
}
