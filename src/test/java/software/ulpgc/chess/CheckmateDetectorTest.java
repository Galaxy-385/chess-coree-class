
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CheckmateDetectorTest {

    @Test
    void shouldDetectCheckmate() {
        Board board = new Board(Map.of(
                Square.at("a8"), Piece.BlackKing,
                Square.at("c6"), Piece.WhiteKing,
                Square.at("b7"), Piece.WhiteQueen
        ));

        CheckmateDetector detector = new CheckmateDetector();

        assertTrue(detector.isCheckmate(board, Color.Black));
    }

    @Test
    void shouldNotDetectCheckmateWhenKingIsNotInCheck() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing
        ));

        CheckmateDetector detector = new CheckmateDetector();

        assertFalse(detector.isCheckmate(board, Color.White));
    }


    @Test
    void shouldNotDetectCheckmateWhenKingCanEscape() {
        Board board = new Board(Map.of(
                Square.at("a8"), Piece.BlackKing,
                Square.at("c6"), Piece.WhiteKing,
                Square.at("b6"), Piece.WhiteQueen
        ));

        CheckmateDetector detector = new CheckmateDetector();

        assertFalse(detector.isCheckmate(board, Color.Black));
    }


    @Test
    void shouldNotDetectCheckmateWhenPawnCanPromoteAndCaptureAttacker() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("a8"), Piece.BlackKing,
                Square.at("e8"), Piece.BlackRook,
                Square.at("d7"), Piece.WhitePawn
        ));

        CheckmateDetector detector = new CheckmateDetector();

        assertFalse(detector.isCheckmate(board, Color.White));
    }

}
