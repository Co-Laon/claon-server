package coLaon.ClaonBack.center.domain.converter;

import coLaon.ClaonBack.center.domain.Charge;
import coLaon.ClaonBack.center.domain.ChargeElement;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChargeListConverter implements AttributeConverter<List<Charge>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Charge> attribute) {
        if (attribute.size() == 0) {
            return "";
        }

        List<String> jsonList = attribute.stream().map(a -> {
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
        }).collect(Collectors.toList());

        return String.join("&&&", jsonList);
    }

    @Override
    public List<Charge> convertToEntityAttribute(String dbData) {
        List<String> jsonList = List.of(dbData.split("&&&"));

        return jsonList.stream().map(json -> {
            try {
                if (json.length() == 0) return null;

                Map<String, String> charge = objectMapper.readValue(json, new TypeReference<>() {});

                return Charge.of(
                        List.of(charge.get("chargeList").split("&&&")).stream().map(c -> {
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
