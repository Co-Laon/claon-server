package com.claon.center.dto;

import com.claon.center.domain.CenterBookmark;
import com.claon.center.domain.CenterImg;
import com.claon.center.domain.Charge;
import com.claon.center.domain.OperatingTime;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CenterBookmarkResponseDto {
    private final String centerId;
    private final String name;
    private final String address;
    private final String tel;
    private final String webUrl;
    private final String instagramUrl;
    private final String youtubeUrl;
    private final List<CenterImg> imgList;
    private final List<OperatingTime> operatingTimeList;
    private final String facilities;
    private final List<Charge> chargeList;
    private final String holdInfoImg;
    private final Boolean isBookmarked;

    private CenterBookmarkResponseDto(
            String centerId,
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
                bookmarkCenter.getCenter().getImgList(),
                bookmarkCenter.getCenter().getOperatingTime(),
                bookmarkCenter.getCenter().getFacilities(),
                bookmarkCenter.getCenter().getCharge(),
                bookmarkCenter.getCenter().getHoldInfoImg(),
                isBookmarked
        );
    }
}
