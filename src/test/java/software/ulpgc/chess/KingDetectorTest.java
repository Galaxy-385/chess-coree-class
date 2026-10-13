
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class KingDetectorTest {

    @Test
    void findsWhiteKing() {
        Board board = Board.initial();
        KingDetector detector = new KingDetector();

        assertEquals(
                Square.at("e1"),
                detector.findKing(board, Color.White)
        );
    }

    @Test
    void findsBlackKing() {
        Board board = Board.initial();
        KingDetector detector = new KingDetector();

        assertEquals(
                Square.at("e8"),
                detector.findKing(board, Color.Black)
        );
    }

    @Test
    void returnsNullWhenKingIsMissing() {
        Board board = new Board(Map.of(
                Square.at("a1"), Piece.WhiteRook
        ));

        KingDetector detector = new KingDetector();

        assertNull(detector.findKing(board, Color.White));
    }
}
