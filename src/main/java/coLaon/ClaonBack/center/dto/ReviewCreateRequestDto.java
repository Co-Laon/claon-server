package coLaon.ClaonBack.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequestDto {
    @NotBlank(message = "점수를 매겨주세요")
    @Max(value = 5) @Min(value = 1)
    private Integer rank;
    @NotBlank(message = "리뷰 내용을 작성해주세요")
    @Size(min = 1, max = 500, message = "리뷰 최대 글자수는 500자입니다")
    private String content;
}
