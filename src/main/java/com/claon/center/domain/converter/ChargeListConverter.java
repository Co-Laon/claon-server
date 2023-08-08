package com.claon.center.domain.converter;

import com.claon.common.exception.ErrorCode;
import com.claon.common.exception.InternalServerErrorException;
import com.claon.center.domain.Charge;
import com.claon.center.domain.ChargeElement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChargeListConverter implements AttributeConverter<List<Charge>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Charge> attribute) {
        if (attribute.size() == 0) {
            return "";
        }

        return attribute.stream()
                .filter(Objects::nonNull)
                .map(a -> {
                    String chargeList = a.getChargeList().stream().map(charge -> {
                        try {
                            return objectMapper.writeValueAsString(charge);
                        } catch (JsonProcessingException e) {
                            throw new InternalServerErrorException(
                                    ErrorCode.INTERNAL_SERVER_ERROR,
                                    ""
                            );
                        }
                    }).collect(Collectors.joining("&&&"));

                    Map<String, String> charge = new HashMap<>();
                    charge.put("chargeList", chargeList);
                    charge.put("image", a.getImage());

                    try {
                        return objectMapper.writeValueAsString(charge);
                    } catch (JsonProcessingException e) {
                        throw new InternalServerErrorException(
                                ErrorCode.INTERNAL_SERVER_ERROR,
                                ""
                        );
                    }
                }).collect(Collectors.joining("&&&&"));
    }

    @Override
    public List<Charge> convertToEntityAttribute(String dbData) {
        List<String> jsonList = List.of(dbData.split("&&&&"));

        return jsonList.stream().map(json -> {
            try {
                if (json.length() == 0) return null;

                Map<String, String> charge = objectMapper.readValue(json, new TypeReference<>() {
                });

                return Charge.of(
                        Stream.of(charge.get("chargeList").split("&&&"))
                                .filter(c -> !c.equals(""))
                                .map(c -> {
                                    try {
                                        return objectMapper.readValue(c, ChargeElement.class);
                                    } catch (JsonProcessingException e) {
                                        throw new InternalServerErrorException(
                                                ErrorCode.INTERNAL_SERVER_ERROR,
                                                e.getMessage()
                                        );
                                    }
                                }).collect(Collectors.toList()),
                        charge.get("image")
                );
            } catch (JsonProcessingException e) {
                throw new InternalServerErrorException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        e.getMessage()
                );
            }
        }).collect(Collectors.toList());
    }
}
