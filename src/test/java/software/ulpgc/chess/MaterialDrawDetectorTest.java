package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MaterialDrawDetectorTest {

    @Test
    void shouldDetectDrawWithOnlyKings() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing
        ));

        MaterialDrawDetector detector = new MaterialDrawDetector();

        assertTrue(detector.isInsufficientMaterial(board));
    }

    @Test
    void shouldDetectDrawWithKingAndBishopAgainstKing() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing,
                Square.at("c1"), Piece.WhiteBishop
        ));

        MaterialDrawDetector detector = new MaterialDrawDetector();

        assertTrue(detector.isInsufficientMaterial(board));
    }

    @Test
    void shouldNotDetectInsufficientMaterialWithQueen() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing,
                Square.at("a1"), Piece.WhiteQueen
        ));

        MaterialDrawDetector detector = new MaterialDrawDetector();

        assertFalse(detector.isInsufficientMaterial(board));
    }

    @Test
    void shouldDetectDrawWithKingAndKnightAgainstKing() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing,
                Square.at("c2"), Piece.WhiteKnight
        ));

        MaterialDrawDetector detector = new MaterialDrawDetector();

        assertTrue(detector.isInsufficientMaterial(board));
    }

    @Test
    void shouldDetectDrawWithBishopsOnSameColorSquares() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing,
                Square.at("c1"), Piece.WhiteBishop,
                Square.at("f8"), Piece.BlackBishop
        ));

        MaterialDrawDetector detector = new MaterialDrawDetector();

        assertTrue(detector.isInsufficientMaterial(board));
    }

    @Test
    void shouldNotDetectDrawWithTwoKnightsAgainstKing() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e8"), Piece.BlackKing,
                Square.at("c2"), Piece.WhiteKnight,
                Square.at("f2"), Piece.WhiteKnight
        ));

        MaterialDrawDetector detector = new MaterialDrawDetector();

        assertFalse(detector.isInsufficientMaterial(board));
    }
}