
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BoardCastlingTest {

    @Test
    void whiteCanCastleKingSide() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("h1"), Piece.WhiteRook
        ));

        Board castledBoard = board.legalMove(
                Square.at("e1"), Square.at("g1")
        );

        assertEquals(Piece.WhiteKing,
                castledBoard.pieceAt(Square.at("g1")));
        assertEquals(Piece.WhiteRook,
                castledBoard.pieceAt(Square.at("f1")));

        assertNull(castledBoard.pieceAt(Square.at("e1")));
        assertNull(castledBoard.pieceAt(Square.at("h1")));
    }

    @Test
    void whiteCanCastleQueenSide() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("a1"), Piece.WhiteRook
        ));

        Board castledBoard = board.legalMove(
                Square.at("e1"), Square.at("c1")
        );

        assertEquals(Piece.WhiteKing,
                castledBoard.pieceAt(Square.at("c1")));
        assertEquals(Piece.WhiteRook,
                castledBoard.pieceAt(Square.at("d1")));
    }

    @Test
    void blackCanCastleKingSide() {
        Board board = new Board(Map.of(
                Square.at("e8"), Piece.BlackKing,
                Square.at("h8"), Piece.BlackRook
        ));

        Board castledBoard = board.legalMove(
                Square.at("e8"), Square.at("g8")
        );

        assertEquals(Piece.BlackKing,
                castledBoard.pieceAt(Square.at("g8")));
        assertEquals(Piece.BlackRook,
                castledBoard.pieceAt(Square.at("f8")));
    }

    @Test
    void blackCanCastleQueenSide() {
        Board board = new Board(Map.of(
                Square.at("e8"), Piece.BlackKing,
                Square.at("a8"), Piece.BlackRook
        ));

        Board castledBoard = board.legalMove(
                Square.at("e8"), Square.at("c8")
        );

        assertEquals(Piece.BlackKing,
                castledBoard.pieceAt(Square.at("c8")));
        assertEquals(Piece.BlackRook,
                castledBoard.pieceAt(Square.at("d8")));
    }
}
