package coLaon.ClaonBack.common.domain.enums;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import java.util.Arrays;
import java.util.List;

public enum BasicLocalArea {
    SEOUL("서울시", Arrays.asList("종로구", "중구", "용산구", "성동구", "광진구", "동대문구", "중랑구", "성북구", "강북구", "도봉구",
            "노원구", "은평구", "서대문구", "마포구", "양천구", "강서구", "구로구", "금천구", "영등포구", "동작구", "관악구", "서초구", "강남구", "송파구", "강동구")),
    GYEONGGI("경기도", Arrays.asList("가평군", "연천군", "동두천시", "안산시", "시흥시", "광주시", "오산시", "양주시", "포천시",
            "용인시", "파주시", "구리시", "이천시", "의정부시", "안양시", "수원시", "성남시", "양평군", "하남시", "부천시",
            "의왕시", "평택시", "군포시", "화성시", "남양주시", "광명시", "고양시", "여주시", "안성시", "과천시", "김포시"));

    private String key;
    private List<String> value;

    BasicLocalArea(String key, List<String> value) {
        this.key = key;
        this.value = value;
    }

    public static String of(String key, String value) {
        return Arrays.stream(values())
                .filter(basicLocalArea -> key.equals(basicLocalArea.key))
                .map(basicLocalArea ->
                    basicLocalArea.value
                            .stream()
                            .filter(v -> v.equals(value))
                            .findFirst()
                            .orElseThrow(
                                    () -> new BadRequestException(
                                            ErrorCode.WRONG_ADDRESS,
                                            "잘못된 주소입니다."
                                    )
                            )
                )
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_ADDRESS,
                                "잘못된 주소입니다."
                        )
                );
    }
}
