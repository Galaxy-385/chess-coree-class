
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CastlingMovementRuleTest {

    private final CastlingMovementRule rule =
            new CastlingMovementRule();

    @Test
    void whiteCanCastleKingSideWhenPathIsClear() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("h1"), Piece.WhiteRook
        ));

        assertTrue(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }

    @Test
    void whiteCanCastleQueenSideWhenPathIsClear() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("a1"), Piece.WhiteRook
        ));

        assertTrue(rule.canMove(
                board, Square.at("e1"), Square.at("c1")
        ));
    }

    @Test
    void blackCanCastleKingSideWhenPathIsClear() {
        Board board = new Board(Map.of(
                Square.at("e8"), Piece.BlackKing,
                Square.at("h8"), Piece.BlackRook
        ));

        assertTrue(rule.canMove(
                board, Square.at("e8"), Square.at("g8")
        ));
    }

    @Test
    void blackCanCastleQueenSideWhenPathIsClear() {
        Board board = new Board(Map.of(
                Square.at("e8"), Piece.BlackKing,
                Square.at("a8"), Piece.BlackRook
        ));

        assertTrue(rule.canMove(
                board, Square.at("e8"), Square.at("c8")
        ));
    }

    @Test
    void cannotCastleWhenPathIsBlocked() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("h1"), Piece.WhiteRook,
                Square.at("f1"), Piece.WhiteBishop
        ));

        assertFalse(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }

    @Test
    void cannotCastleWithoutRook() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing
        ));

        assertFalse(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }

    @Test
    void cannotCastleIfKingHasLostCastlingRights() {
        Board board = new Board(
                Map.of(
                        Square.at("e1"), Piece.WhiteKing,
                        Square.at("h1"), Piece.WhiteRook
                ),
                CastlingRights.initial().withoutWhiteCastling()
        );

        assertFalse(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }

    @Test
    void cannotCastleWithNonKingPiece() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteQueen,
                Square.at("h1"), Piece.WhiteRook
        ));

        assertFalse(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }


    @Test
    void cannotCastleWhenKingIsInCheck() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("h1"), Piece.WhiteRook,
                Square.at("e8"), Piece.BlackRook
        ));

        assertFalse(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }

    @Test
    void cannotCastleThroughAttackedSquare() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("h1"), Piece.WhiteRook,
                Square.at("f8"), Piece.BlackRook
        ));

        assertFalse(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }



    @Test
    void cannotCastleToAttackedSquare() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("h1"), Piece.WhiteRook,
                Square.at("g8"), Piece.BlackRook
        ));

        assertFalse(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }


    @Test
    void cannotCastleIfRookHasLostCastlingRights() {
        Board board = new Board(
                Map.of(
                        Square.at("e1"), Piece.WhiteKing,
                        Square.at("h1"), Piece.WhiteRook
                ),
                CastlingRights.initial().withoutWhiteKingSide()
        );

        assertFalse(rule.canMove(
                board, Square.at("e1"), Square.at("g1")
        ));
    }



}
