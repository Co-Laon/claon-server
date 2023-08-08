package com.claon.center.domain.converter;

import com.claon.common.exception.ErrorCode;
import com.claon.common.exception.InternalServerErrorException;
import com.claon.center.domain.CenterImg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.util.List;
import java.util.stream.Collectors;

public class CenterImgListConverter implements AttributeConverter<List<CenterImg>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<CenterImg> attribute) {
        if (attribute.size() == 0) {
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
    public List<CenterImg> convertToEntityAttribute(String dbData) {
        List<String> jsonList = List.of(dbData.split("&&&"));

        return jsonList.stream().map(json -> {
            try {
                if (json.length() == 0) return null;

                return objectMapper.readValue(json, CenterImg.class);
            } catch (JsonProcessingException e) {
                throw new InternalServerErrorException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        e.getMessage()
                );
            }
        }).collect(Collectors.toList());
    }
}
