package com.claon.center.domain.converter;

import com.claon.common.exception.ErrorCode;
import com.claon.common.exception.InternalServerErrorException;
import com.claon.center.domain.OperatingTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.util.List;
import java.util.stream.Collectors;

public class OperatingTimeListConverter implements AttributeConverter<List<OperatingTime>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<OperatingTime> attribute) {
        if (attribute.isEmpty()) {
            return "";
        }

        return attribute.stream().map(a -> {
            try {
                return objectMapper.writeValueAsString(a);
            } catch (JsonProcessingException e) {
                throw new InternalServerErrorException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        ""
                );
            }
        }).collect(Collectors.joining("&&&"));
    }

    @Override
    public List<OperatingTime> convertToEntityAttribute(String dbData) {
        List<String> jsonList = List.of(dbData.split("&&&"));

        return jsonList.stream().map(json -> {
            try {
                if (json.isEmpty()) return null;

                return objectMapper.readValue(json, OperatingTime.class);
            } catch (JsonProcessingException e) {
                throw new InternalServerErrorException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        e.getMessage()
                );
            }
        }).collect(Collectors.toList());
    }
}
