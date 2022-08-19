package coLaon.ClaonBack.center.domain.converter;

import coLaon.ClaonBack.center.domain.OperatingTime;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.util.List;
import java.util.stream.Collectors;

public class OperatingTimeListConverter implements AttributeConverter<List<OperatingTime>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<OperatingTime> attribute) {
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
    public List<OperatingTime> convertToEntityAttribute(String dbData) {
        List<String> jsonList = List.of(dbData.split("&&&"));

        return jsonList.stream().map(json -> {
            try {
                if (json.length() == 0) return null;

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
