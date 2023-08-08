package com.claon.center.dto;

import com.claon.center.domain.Charge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeDto {
    private List<ChargeElementDto> chargeList;
    private String image;

    public static ChargeDto from(Charge charge) {
        return new ChargeDto(
                charge.getChargeList().stream().map(ChargeElementDto::from).collect(Collectors.toList()),
                charge.getImage()
        );
    }
}
