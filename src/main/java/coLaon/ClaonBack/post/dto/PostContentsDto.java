package coLaon.ClaonBack.post.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostContentsDto {
    @NotBlank(message = "이미지 혹은 동영상을 입력해주세요.")
    String url;
}
