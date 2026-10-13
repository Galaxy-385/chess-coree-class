
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AttackDetectorTest {

    private final AttackDetector detector = new AttackDetector();

    @Test
    void detectsRookAttack() {
        Board board = new Board(Map.of(
                Square.at("a1"), Piece.WhiteRook
        ));

        assertTrue(detector.isSquareAttacked(
                board, Square.at("a8"), Color.White
        ));
    }

    @Test
    void blockedRookDoesNotAttackThroughPiece() {
        Board board = new Board(Map.of(
                Square.at("a1"), Piece.WhiteRook,
                Square.at("a4"), Piece.WhitePawn
        ));

        assertFalse(detector.isSquareAttacked(
                board, Square.at("a8"), Color.White
        ));
    }

    @Test
    void detectsBishopAttack() {
        Board board = new Board(Map.of(
                Square.at("c1"), Piece.WhiteBishop
        ));

        assertTrue(detector.isSquareAttacked(
                board, Square.at("g5"), Color.White
        ));
    }

    @Test
    void detectsKnightAttack() {
        Board board = new Board(Map.of(
                Square.at("e4"), Piece.WhiteKnight
        ));

        assertTrue(detector.isSquareAttacked(
                board, Square.at("f6"), Color.White
        ));
    }

    @Test
    void detectsWhitePawnDiagonalAttackOnEmptySquare() {
        Board board = new Board(Map.of(
                Square.at("e4"), Piece.WhitePawn
        ));

        assertTrue(detector.isSquareAttacked(
                board, Square.at("f5"), Color.White
        ));
    }

    @Test
    void detectsBlackPawnDiagonalAttackOnEmptySquare() {
        Board board = new Board(Map.of(
                Square.at("e5"), Piece.BlackPawn
        ));

        assertTrue(detector.isSquareAttacked(
                board, Square.at("d4"), Color.Black
        ));
    }

    @Test
    void doesNotDetectAttackFromWrongColor() {
        Board board = new Board(Map.of(
                Square.at("a1"), Piece.WhiteRook
        ));

        assertFalse(detector.isSquareAttacked(
                board, Square.at("a8"), Color.Black
        ));
    }

    @Test
    void detectsKingAttackOnAdjacentSquare() {
        Board board = new Board(Map.of(
                Square.at("e4"), Piece.WhiteKing
        ));

        assertTrue(detector.isSquareAttacked(
                board, Square.at("e5"), Color.White
        ));
    }
}
