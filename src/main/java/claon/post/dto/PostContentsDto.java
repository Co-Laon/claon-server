package claon.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostContentsDto {
    @NotBlank(message = "이미지를 업로드 해주세요.")
    @Pattern(regexp = "(\\S+(\\.(?i)(jpe?g|png))$)", message = "이미지만 업로드 해주세요.")
    private String url;
}
