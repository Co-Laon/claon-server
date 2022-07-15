package coLaon.ClaonBack.user.dto;

import lombok.Data;

@Data
public class PublicScopeResponseDto {
    private Boolean isPrivate;

    private PublicScopeResponseDto(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public static PublicScopeResponseDto from(Boolean isPrivate) {
        return new PublicScopeResponseDto(isPrivate);
    }
}
