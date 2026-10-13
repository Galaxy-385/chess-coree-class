
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Map;

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

    @Test
    void enPassantCaptureRemovesCapturedPawn() {
        Board board = new Board(Map.of(
                Square.at("e5"), Piece.WhitePawn,
                Square.at("d5"), Piece.BlackPawn
        ), CastlingRights.initial());

        // simulate the target created by black's two-square move
        board = new Board(
                Map.of(
                        Square.at("e5"), Piece.WhitePawn,
                        Square.at("d5"), Piece.BlackPawn
                ),
                CastlingRights.initial(),
                Square.at("d6")
        );

        Board result = board.legalMove(
                Square.at("e5"),
                Square.at("d6")
        );

        assertEquals(Piece.WhitePawn, result.pieceAt(Square.at("d6")));
        assertNull(result.pieceAt(Square.at("e5")));
        assertNull(result.pieceAt(Square.at("d5")));
    }

    @Test
    void whitePawnCanCaptureEnPassant() {
        Board board = new Board(
                Map.of(
                        Square.at("e5"), Piece.WhitePawn,
                        Square.at("d5"), Piece.BlackPawn
                ),
                CastlingRights.initial(),
                Square.at("d6")
        );

        Board result = board.legalMove(
                Square.at("e5"),
                Square.at("d6")
        );

        assertEquals(Piece.WhitePawn, result.pieceAt(Square.at("d6")));
        assertNull(result.pieceAt(Square.at("d5")));
        assertNull(result.pieceAt(Square.at("e5")));
    }
}
