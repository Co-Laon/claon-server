package com.claon.center.domain;

import com.claon.center.common.domain.BaseEntity;
import com.claon.center.domain.converter.CenterImgListConverter;
import com.claon.center.domain.converter.ChargeListConverter;
import com.claon.center.domain.converter.OperatingTimeListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "tb_center")
@NoArgsConstructor
public class Center extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "tel")
    private String tel;

    @Column(name = "web_url", length = 500)
    private String webUrl;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Convert(converter = CenterImgListConverter.class)
    @Column(name = "img_list", length = 2000)
    private List<CenterImg> imgList;

    @Convert(converter = OperatingTimeListConverter.class)
    @Column(name = "operating_time", length = 1000)
    private List<OperatingTime> operatingTime;

    @Column(name = "facilities")
    private String facilities;

    @Convert(converter = ChargeListConverter.class)
    @Column(name = "charge", length = 3000)
    private List<Charge> charge;

    @Column(name = "hold_info_img", length = 500)
    private String holdInfoImg;

    private Center(
            String name,
            String address,
            String tel,
            String webUrl,
            String instagramUrl,
            String youtubeUrl,
            List<CenterImg> imgList,
            List<OperatingTime> operatingTime,
            String facilities,
            List<Charge> charge,
            String holdInfoImg
    ) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.webUrl = webUrl;
        this.instagramUrl = instagramUrl;
        this.youtubeUrl = youtubeUrl;
        this.imgList = imgList;
        this.operatingTime = operatingTime;
        this.facilities = facilities;
        this.charge = charge;
        this.holdInfoImg = holdInfoImg;
    }

    public static Center of(
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
            String holdInfoImg
    ) {
        return new Center(
                name,
                address,
                tel,
                webUrl,
                instagramUrl,
                youtubeUrl,
                imgList,
                operatingTimeList,
                facilities,
                chargeList,
                holdInfoImg
        );
    }
}
