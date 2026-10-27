
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CheckDetectorTest {

    private final CheckDetector detector = new CheckDetector();

    @Test
    void detectsCheckFromBlackRook() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackRook
        ));

        assertTrue(detector.isInCheck(board, Color.White));
    }

    @Test
    void detectsCheckFromWhiteRook() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteRook,
                Square.at("e8"), Piece.BlackKing
        ));

        assertTrue(detector.isInCheck(board, Color.Black));
    }

    @Test
    void returnsFalseWhenKingIsNotInCheck() {
        Board board = Board.initial();

        assertFalse(detector.isInCheck(board, Color.White));
        assertFalse(detector.isInCheck(board, Color.Black));
    }
}
