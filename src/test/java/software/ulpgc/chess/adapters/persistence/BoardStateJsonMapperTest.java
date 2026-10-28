
package software.ulpgc.chess.adapters.persistence;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardStateJsonMapperTest {

    @Test
    void shouldConvertMapToJsonAndBack() {
        BoardStateJsonMapper mapper = new BoardStateJsonMapper();

        Map<String, String> original = Map.of(
                "e1", "WhiteKing",
                "d8", "BlackQueen"
        );

        String json = mapper.toJson(original);
        Map<String, String> restored = mapper.fromJson(json);

        assertEquals(original, restored);
    }
}
