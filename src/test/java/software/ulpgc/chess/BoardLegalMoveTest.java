
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoardLegalMoveTest {

    @Test
    void legalMoveMovesPieceToDestination() {
        Board board = Board.initial();

        Board movedBoard = board.legalMove(
                Square.at("b1"),
                Square.at("c3")
        );

        assertEquals(Piece.WhiteKnight,
                movedBoard.pieceAt(Square.at("c3")));
        assertNull(movedBoard.pieceAt(Square.at("b1")));
    }

    @Test
    void illegalMoveThrowsException() {
        Board board = Board.initial();

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(
                        Square.at("b1"),
                        Square.at("b3")
                )
        );
    }

    @Test
    void originalBoardRemainsUnchanged() {
        Board board = Board.initial();

        board.legalMove(
                Square.at("b1"),
                Square.at("c3")
        );

        assertEquals(Piece.WhiteKnight,
                board.pieceAt(Square.at("b1")));
        assertNull(board.pieceAt(Square.at("c3")));
    }
}
