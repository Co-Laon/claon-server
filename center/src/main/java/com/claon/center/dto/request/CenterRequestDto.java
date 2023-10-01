package com.claon.center.dto.request;

import com.claon.center.domain.CenterImg;
import com.claon.center.domain.Charge;
import com.claon.center.domain.OperatingTime;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CenterRequestDto(
        @NotBlank(message = "이름을 입력해주세요.")
        String name,
        @NotBlank(message = "주소를 입력해주세요.")
        String address,
        String tel,
        String webUrl,
        String instagramUrl,
        String youtubeUrl,
        List<CenterImg> imgList,
        List<OperatingTime> operatingTimeList,
        String facilities,
        List<Charge> chargeList,
        List<HoldInfoRequestDto> holdInfoList,
        String holdInfoImg,
        List<SectorInfoRequestDto> sectorInfoList
) {
}
