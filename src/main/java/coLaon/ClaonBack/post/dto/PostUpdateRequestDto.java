package coLaon.ClaonBack.post.dto;

import coLaon.ClaonBack.post.dto.validator.ClimbingHistorySize;
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
    @ClimbingHistorySize(message = "1개 이상 10개 이하의 등반 기록을 입력 가능합니다.")
    private List<ClimbingHistoryRequestDto> climbingHistories;
    private String content;
    @Valid
    @NotNull(message = "이미지 혹은 동영상을 업로드해야 합니다.")
    @Size(min = 1, max = 10, message = "이미지 혹은 동영상 1개 이상 10개 이하로 업로드해야 합니다.")
    private List<PostContentsDto> contentsList;
}
