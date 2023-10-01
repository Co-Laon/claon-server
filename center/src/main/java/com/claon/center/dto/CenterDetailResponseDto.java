package com.claon.center.dto;

import com.claon.center.domain.*;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class CenterDetailResponseDto {
    private final String id;
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
    private final List<HoldInfoResponseDto> holdInfoList;
    private final String holdInfoImg;
    private final List<SectorInfoResponseDto> sectorInfoList;
    private final Boolean isBookmarked;
    private final Long postCount;
    private final Long reviewCount;

    private CenterDetailResponseDto(
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
            List<HoldInfoResponseDto> holdInfoList,
            String holdInfoImg,
            List<SectorInfoResponseDto> sectorInfoList,
            Boolean isBookmarked,
            Long postCount,
            Long reviewCount
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
        this.holdInfoList = holdInfoList;
        this.holdInfoImg = holdInfoImg;
        this.sectorInfoList = sectorInfoList;
        this.isBookmarked = isBookmarked;
        this.postCount = postCount;
        this.reviewCount = reviewCount;
    }

    public static CenterDetailResponseDto from(
            Center center,
            List<HoldInfo> holdInfoList,
            List<SectorInfo> sectorInfoList,
            Boolean isBookmarked,
            Long postCount,
            Long reviewCount
    ) {
        return new CenterDetailResponseDto(
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
                holdInfoList.stream().map(HoldInfoResponseDto::from).collect(Collectors.toList()),
                center.getHoldInfoImg(),
                sectorInfoList.stream().map(SectorInfoResponseDto::from).collect(Collectors.toList()),
                isBookmarked,
                postCount,
                reviewCount
        );
    }
}
