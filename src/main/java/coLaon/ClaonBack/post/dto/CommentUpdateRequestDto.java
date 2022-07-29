package coLaon.ClaonBack.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequestDto {
    @NotBlank(message = "댓글을 입력하세요")
    @Size(min = 1, max = 255, message = "댓글 최대 글자수는 255자입니다")
    private String content;
}
