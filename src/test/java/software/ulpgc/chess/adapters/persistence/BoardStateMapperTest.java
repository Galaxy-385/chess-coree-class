
package software.ulpgc.chess.adapters.persistence;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.Board;
import software.ulpgc.chess.domain.CastlingRights;
import software.ulpgc.chess.domain.Piece;
import software.ulpgc.chess.domain.Square;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoardStateMapperTest {

    @Test
    void shouldRestorePiecesFromMap() {
        BoardStateMapper mapper = new BoardStateMapper();

        Map<String, String> state = Map.of(
                "e1", "WhiteKing",
                "d1", "WhiteQueen",
                "e8", "BlackKing",
                "d8", "BlackQueen"
        );

        Board restored = mapper.fromMap(
                state,
                CastlingRights.initial(),
                null
        );

        assertEquals(Piece.WhiteKing, restored.pieceAt(Square.at("e1")));
        assertEquals(Piece.WhiteQueen, restored.pieceAt(Square.at("d1")));
        assertEquals(Piece.BlackKing, restored.pieceAt(Square.at("e8")));
        assertEquals(Piece.BlackQueen, restored.pieceAt(Square.at("d8")));
        assertEquals(4, restored.pieceCount());
    }

    @Test
    void shouldConvertInitialBoardToMapAndBack() {
        BoardStateMapper mapper = new BoardStateMapper();
        Board original = Board.initial();

        Map<String, String> state = mapper.toMap(original);

        Board restored = mapper.fromMap(
                state,
                original.castlingRights(),
                original.enPassantTarget()
        );

        assertEquals(original.pieceCount(), restored.pieceCount());

        for (software.ulpgc.chess.domain.File file
                : software.ulpgc.chess.domain.File.values()) {
            for (software.ulpgc.chess.domain.Rank rank
                    : software.ulpgc.chess.domain.Rank.values()) {

                Square square = new Square(file, rank);

                assertEquals(
                        original.pieceAt(square),
                        restored.pieceAt(square)
                );
            }
        }
    }
}
