package coLaon.ClaonBack.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDto {
    private String centerName;
    private String holdInfo;
    private String content;
    List<PostContentsDto> contentsList;
}
