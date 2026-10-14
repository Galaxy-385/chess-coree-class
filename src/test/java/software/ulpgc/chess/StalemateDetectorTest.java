package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StalemateDetectorTest {

    @Test
    void shouldDetectStalemate() {
        Board board = new Board(Map.of(
                Square.at("a8"), Piece.BlackKing,
                Square.at("c6"), Piece.WhiteKing,
                Square.at("c7"), Piece.WhiteQueen
        ));

        StalemateDetector detector = new StalemateDetector();

        assertTrue(detector.isStalemate(board, Color.Black));
    }

    @Test
    void shouldNotDetectStalemateWhenKingIsInCheck() {
        Board board = new Board(Map.of(
                Square.at("a8"), Piece.BlackKing,
                Square.at("c6"), Piece.WhiteKing,
                Square.at("b7"), Piece.WhiteQueen
        ));

        StalemateDetector detector = new StalemateDetector();

        assertFalse(detector.isStalemate(board, Color.Black));
    }


    @Test
    void shouldNotDetectStalemateWhenPawnCanPromote() {
        Board board = new Board(Map.of(
                Square.at("a1"), Piece.WhiteKing,
                Square.at("h8"), Piece.BlackKing,
                Square.at("b7"), Piece.WhitePawn
        ));

        StalemateDetector detector = new StalemateDetector();

        assertFalse(detector.isStalemate(board, Color.White));
    }

}