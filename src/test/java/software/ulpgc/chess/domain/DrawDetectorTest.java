package software.ulpgc.chess.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DrawDetectorTest {

    @Test
    void shouldDetectDrawByInsufficientMaterial() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing
        ));

        DrawDetector detector = new DrawDetector();

        assertTrue(detector.isDraw(board, Color.White));
    }

    @Test
    void shouldNotDetectDrawWhenMaterialIsSufficient() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing,
                Square.at("a1"), Piece.WhiteQueen
        ));

        DrawDetector detector = new DrawDetector();

        assertFalse(detector.isDraw(board, Color.White));
    }
}