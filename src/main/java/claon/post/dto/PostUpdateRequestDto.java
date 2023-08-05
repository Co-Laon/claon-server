package claon.post.dto;

import claon.post.dto.validator.ClimbingHistorySize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequestDto {
    @Valid
    @ClimbingHistorySize(message = "1-10회 등반 기록을 입력해주세요.")
    private List<ClimbingHistoryRequestDto> climbingHistories;
    @Size(max = 500, message = "500자 이내로 내용을 입력해주세요.")
    private String content;
    @Valid
    @NotNull(message = "이미지를 업로드 해주세요.")
    @Size(min = 1, max = 10, message = "1-10개 이미지를 업로드 해주세요.")
    private List<PostContentsDto> contentsList;
}
