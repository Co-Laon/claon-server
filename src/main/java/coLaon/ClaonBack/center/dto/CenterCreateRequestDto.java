package coLaon.ClaonBack.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterCreateRequestDto {
    @NotBlank(message = "암장 이름을 입력 해주세요.")
    private String name;
    @NotBlank(message = "암장 주소를 입력 해주세요.")
    private String address;
    private String tel;
    private String webUrl;
    private String instagramUrl;
    private String youtubeUrl;
    private List<CenterImgDto> imgList;
    private List<OperatingTimeDto> operatingTimeList;
    private String facilities;
    private List<ChargeDto> chargeList;
    private List<HoldInfoRequestDto> holdInfoList;
    private String holdInfoImg;
    private List<SectorInfoDto> sectorInfoList;

    public List<HoldInfoRequestDto> getHoldInfoList() {
        return Objects.requireNonNullElse(this.holdInfoList, Collections.emptyList());
    }

    public List<SectorInfoDto> getSectorInfoList() {
        return Objects.requireNonNullElse(this.sectorInfoList, Collections.emptyList());
    }
}
