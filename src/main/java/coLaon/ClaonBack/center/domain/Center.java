package coLaon.ClaonBack.center.domain;

import coLaon.ClaonBack.center.domain.converter.CenterImgListConverter;
import coLaon.ClaonBack.center.domain.converter.ChargeListConverter;
import coLaon.ClaonBack.center.domain.converter.OperatingTimeListConverter;
import coLaon.ClaonBack.center.domain.converter.SectorInfoListConverter;
import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    @Convert(converter = SectorInfoListConverter.class)
    @Column(name = "sector_info", length = 2000)
    private List<SectorInfo> sectorInfo;

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
            String holdInfoImg,
            List<SectorInfo> sectorInfo
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
        this.sectorInfo = sectorInfo;
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
            String holdInfoImg,
            List<SectorInfo> sectorInfoList
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
                holdInfoImg,
                sectorInfoList
        );
    }
}
