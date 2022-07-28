package coLaon.ClaonBack.center.dto;

import coLaon.ClaonBack.center.domain.BookmarkCenter;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class BookmarkCenterResponseDto {
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
    private String chargeImg;
    private String holdInfoImg;
    private List<SectorInfoDto> sectorInfoList;
    private Boolean isBookmarked;

    private BookmarkCenterResponseDto(String centerId,
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
                                     String chargeImg,
                                     String holdInfoImg,
                                     List<SectorInfoDto> sectorInfoList,
                                     Boolean isBookmarked) {
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
        this.chargeImg = chargeImg;
        this.holdInfoImg = holdInfoImg;
        this.sectorInfoList = sectorInfoList;
        this.isBookmarked = isBookmarked;
    }

    public static BookmarkCenterResponseDto from(BookmarkCenter bookmarkCenter, Boolean isBookmarked) {
        return new BookmarkCenterResponseDto(
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
                bookmarkCenter.getCenter().getChargeImg(),
                bookmarkCenter.getCenter().getHoldInfoImg(),
                bookmarkCenter.getCenter().getSectorInfo().stream().map(SectorInfoDto::from).collect(Collectors.toList()),
                isBookmarked
        );
    }
}
