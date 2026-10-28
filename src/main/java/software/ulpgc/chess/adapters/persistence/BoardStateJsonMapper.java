
package software.ulpgc.chess.adapters.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class BoardStateJsonMapper {

    private final ObjectMapper objectMapper;

    public BoardStateJsonMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public String toJson(Map<String, String> state) {
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Could not serialize board state",
                    exception
            );
        }
    }

    public Map<String, String> fromJson(String json) {
        try {
            return objectMapper.readValue(
                    json,
                    new TypeReference<Map<String, String>>() {}
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Could not deserialize board state",
                    exception
            );
        }
    }
}
