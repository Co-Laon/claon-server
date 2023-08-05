package claon.center.dto;

import claon.center.domain.CenterBookmark;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CenterBookmarkResponseDto {
    private String centerId;
    private String name;
    private String address;
    private String tel;
    private String webUrl;
    private String instagramUrl;
    private String youtubeUrl;
    private List<CenterImgDto> imgList;
    private List<OperatingTimeDto> operatingTimeList;
    private String facilities;
    private List<ChargeDto> chargeList;
    private String holdInfoImg;
    private List<SectorInfoResponseDto> sectorInfoList;
    private Boolean isBookmarked;

    private CenterBookmarkResponseDto(
            String centerId,
            String name,
            String address,
            String tel,
            String webUrl,
            String instagramUrl,
            String youtubeUrl,
            List<CenterImgDto> imgList,
            List<OperatingTimeDto> operatingTimeList,
            String facilities,
            List<ChargeDto> chargeList,
            String holdInfoImg,
            Boolean isBookmarked
    ) {
        this.centerId = centerId;
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
        this.holdInfoImg = holdInfoImg;
        this.isBookmarked = isBookmarked;
    }

    public static CenterBookmarkResponseDto from(CenterBookmark bookmarkCenter, Boolean isBookmarked) {
        return new CenterBookmarkResponseDto(
                bookmarkCenter.getCenter().getId(),
                bookmarkCenter.getCenter().getName(),
                bookmarkCenter.getCenter().getAddress(),
                bookmarkCenter.getCenter().getTel(),
                bookmarkCenter.getCenter().getWebUrl(),
                bookmarkCenter.getCenter().getInstagramUrl(),
                bookmarkCenter.getCenter().getYoutubeUrl(),
                bookmarkCenter.getCenter().getImgList().stream().map(CenterImgDto::from).collect(Collectors.toList()),
                bookmarkCenter.getCenter().getOperatingTime().stream().map(OperatingTimeDto::from).collect(Collectors.toList()),
                bookmarkCenter.getCenter().getFacilities(),
                bookmarkCenter.getCenter().getCharge().stream().map(ChargeDto::from).collect(Collectors.toList()),
                bookmarkCenter.getCenter().getHoldInfoImg(),
                isBookmarked
        );
    }
}
