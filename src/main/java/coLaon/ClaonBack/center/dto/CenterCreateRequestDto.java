package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

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
    private List<CenterImg> imgList;
    private List<OperatingTime> operatingTimeList;
    private String facilities;
    private List<Charge> chargeList;
    private String chargeImg;
    private List<HoldInfoRequestDto> holdInfoList;
    private String holdInfoImg;
    private List<SectorInfo> sectorInfoList;
}
