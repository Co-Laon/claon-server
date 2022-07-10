package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.domain.PostContents;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDto {
    private String centerName;
    private String holdName;
    private String content;
    Set<PostContents> contentsSet;
}
