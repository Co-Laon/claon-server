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
    @Column(name = "web_url")
    private String webUrl;
    @Column(name = "instagram_url")
    private String instagramUrl;
    @Column(name = "youtube_url")
    private String youtubeUrl;
    @Convert(converter = CenterImgListConverter.class)
    @Column(name = "img_list")
    private List<CenterImg> imgList;
    @Convert(converter = OperatingTimeListConverter.class)
    @Column(name = "operating_time")
    private List<OperatingTime> operatingTime;
    @Column(name = "facilities")
    private String facilities;
    @Convert(converter = ChargeListConverter.class)
    @Column(name = "charge")
    private List<Charge> charge;
    @Column(name = "charge_img")
    private String chargeImg;
    @Column(name = "hold_info_img")
    private String holdInfoImg;
    @Convert(converter = SectorInfoListConverter.class)
    @Column(name = "sector_info")
    private List<SectorInfo> sectorInfo;
    @Column(name = "review_rank")
    private float reviewRank;

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
            String chargeImg,
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
        this.chargeImg = chargeImg;
        this.holdInfoImg = holdInfoImg;
        this.sectorInfo = sectorInfo;
    }

    private Center(
            String id,
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
            String chargeImg,
            String holdInfoImg,
            List<SectorInfo> sectorInfo
    ) {
        super(id);
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
        this.chargeImg = chargeImg;
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
            String chargeImg,
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
                chargeImg,
                holdInfoImg,
                sectorInfoList
        );
    }

    public static Center of(
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
            String holdInfoImg,
            List<SectorInfo> sectorInfoList
    ) {
        return new Center(
                id,
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
                chargeImg,
                holdInfoImg,
                sectorInfoList
        );
    }

    public void addRank(List<Integer> ranks, Integer addRank, Integer reviewerCount) {
        this.reviewRank = (float) (ranks.stream().reduce(0, Integer::sum) + addRank) / (reviewerCount + 1);
    }

    public void changeRank(List<Integer> ranks, Integer rank, Integer updateRank, Integer reviewerCount) {
        this.reviewRank = (float) (ranks.stream().reduce(0, Integer::sum) - rank + updateRank) / reviewerCount;
    }

    public void deleteRank(List<Integer> ranks, Integer deleteRank, Integer reviewerCount) {
        if (reviewerCount == 1) {
            this.reviewRank = 0;
        }
        else {
            this.reviewRank = (float) (ranks.stream().reduce(0, Integer::sum) - deleteRank) / (reviewerCount - 1);
        }
    }

    public void updateRank(float rank) {
        this.reviewRank = rank;
    }
}
