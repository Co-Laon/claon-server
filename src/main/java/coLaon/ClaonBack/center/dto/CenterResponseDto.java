package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.Center;
import coLaon.ClaonBack.center.domain.CenterImg;
import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.center.domain.SectorInfo;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CenterResponseDto {
    private String id;
    private String name;
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
    private List<HoldInfoResponseDto> holdInfoList;
    private String holdInfoImg;
    private List<SectorInfo> sectorInfoList;

    private CenterResponseDto(
            String id,
            String name,
            String address,
            String tel,
            String webUrl,
            String instagramUrl,
            String youtubeUrl,
            List<CenterImg> imgList,
            List<OperatingTime> operatingTimeList,
            String facilities,
            List<Charge> chargeList,
            String chargeImg,
            List<HoldInfoResponseDto> holdInfoList,
            String holdInfoImg,
            List<SectorInfo> sectorInfoList
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.webUrl = webUrl;
        this.instagramUrl = instagramUrl;
        this.youtubeUrl = youtubeUrl;
        this.imgList = imgList;
        this.operatingTimeList = operatingTimeList;
        this.facilities = facilities;
        this.chargeList = chargeList;
        this.chargeImg = chargeImg;
        this.holdInfoList = holdInfoList;
        this.holdInfoImg = holdInfoImg;
        this.sectorInfoList = sectorInfoList;
    }

    public static CenterResponseDto from(Center center, List<HoldInfo> holdInfoList) {
        return new CenterResponseDto(
                center.getId(),
                center.getName(),
                center.getAddress(),
                center.getTel(),
                center.getWebUrl(),
                center.getInstagramUrl(),
                center.getYoutubeUrl(),
                center.getImgList(),
                center.getOperatingTime(),
                center.getFacilities(),
                center.getCharge(),
                center.getChargeImg(),
                holdInfoList.stream().map(HoldInfoResponseDto::from).collect(Collectors.toList()),
                center.getHoldInfoImg(),
                center.getSectorInfo()
        );
    }
}
