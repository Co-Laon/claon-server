package coLaon.ClaonBack.post.domain.converter;

import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.InternalServerErrorException;
import coLaon.ClaonBack.post.domain.PostContents;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.util.List;
import java.util.stream.Collectors;

public class PostContentsConverter implements AttributeConverter<List<PostContents>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<PostContents> attribute) {
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
    public List<PostContents> convertToEntityAttribute(String dbData) {
        List<String> jsonList = List.of(dbData.split("&&&"));

        return jsonList.stream().map(json -> {
            try {
                if (json.length() == 0) return null;

                return objectMapper.readValue(json, PostContents.class);
            } catch (JsonProcessingException e) {
                throw new InternalServerErrorException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        e.getMessage()
                );
            }
        }).collect(Collectors.toList());
    }
}
