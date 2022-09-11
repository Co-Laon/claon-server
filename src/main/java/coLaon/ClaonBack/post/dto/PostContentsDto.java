package coLaon.ClaonBack.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostContentsDto {
    @NotBlank(message = "이미지 혹은 동영상을 입력해주세요.")
    @Pattern(regexp = "(\\S+(\\.(?i)(jpe?g|png))$)", message = "이미지 혹은 동영상 형식이 아닙니다.")
    private String url;
}
