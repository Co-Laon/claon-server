package coLaon.ClaonBack.center.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

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

    public Optional<List<HoldInfoRequestDto>> getHoldInfoList() {
        return Optional.ofNullable(this.holdInfoList);
    }

    public Optional<List<SectorInfoDto>> getSectorInfoList() {
        return Optional.ofNullable(this.sectorInfoList);
    }
}
