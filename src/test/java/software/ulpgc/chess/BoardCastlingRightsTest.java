
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardCastlingRightsTest {

    @Test
    void initialBoardAllowsAllCastlingRights() {
        Board board = Board.initial();

        CastlingRights rights = board.castlingRights();

        assertTrue(rights.whiteKingSide());
        assertTrue(rights.whiteQueenSide());
        assertTrue(rights.blackKingSide());
        assertTrue(rights.blackQueenSide());
    }

    @Test
    void movingWhiteKingRemovesBothWhiteRights() {
        Board board = Board.initial();

        Board movedBoard = board.move(
                Square.at("e1"),
                Square.at("e2")
        );

        assertFalse(movedBoard.castlingRights().whiteKingSide());
        assertFalse(movedBoard.castlingRights().whiteQueenSide());

        assertTrue(movedBoard.castlingRights().blackKingSide());
        assertTrue(movedBoard.castlingRights().blackQueenSide());
    }

    @Test
    void movingWhiteKingsideRookRemovesOnlyItsRight() {
        Board board = Board.initial();

        Board movedBoard = board.move(
                Square.at("h1"),
                Square.at("h2")
        );

        assertFalse(movedBoard.castlingRights().whiteKingSide());
        assertTrue(movedBoard.castlingRights().whiteQueenSide());
    }
}
