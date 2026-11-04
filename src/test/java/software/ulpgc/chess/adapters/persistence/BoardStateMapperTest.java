
package software.ulpgc.chess.adapters.persistence;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.model.*;
import software.ulpgc.chess.domain.state.CastlingRights;

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

        for (File file
                : File.values()) {
            for (Rank rank
                    : Rank.values()) {

                Square square = new Square(file, rank);

                assertEquals(
                        original.pieceAt(square),
                        restored.pieceAt(square)
                );
            }
        }
    }


    @Test
    void shouldRestoreBoardWithEnPassantTarget() {
        Board original = Board.initial()
                .legalMove(Square.at("e2"), Square.at("e4"));

        BoardStateMapper mapper = new BoardStateMapper();

        Board restored = mapper.fromMap(
                mapper.toMap(original),
                original.castlingRights(),
                original.enPassantTarget()
        );

        assertEquals(
                original.enPassantTarget(),
                restored.enPassantTarget()
        );

        assertEquals(
                original.castlingRights().whiteKingSide(),
                restored.castlingRights().whiteKingSide()
        );

        assertEquals(
                original.castlingRights().blackQueenSide(),
                restored.castlingRights().blackQueenSide()
        );
    }
}
