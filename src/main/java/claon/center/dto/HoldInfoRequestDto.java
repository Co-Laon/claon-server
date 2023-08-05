package claon.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HoldInfoRequestDto {
    @NotBlank(message = "홀드 이름을 입력 해주세요.")
    private String name;
    @NotBlank(message = "홀드 이미지를 입력 해주세요.")
    private String img;
}
