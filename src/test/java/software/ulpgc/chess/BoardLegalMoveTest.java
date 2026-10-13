
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

    @Test
    void blackPawnCanCaptureEnPassant() {
        Board board = new Board(
                Map.of(
                        Square.at("e4"), Piece.BlackPawn,
                        Square.at("d4"), Piece.WhitePawn
                ),
                CastlingRights.initial(),
                Square.at("d3")
        );

        Board result = board.legalMove(
                Square.at("e4"),
                Square.at("d3")
        );

        assertEquals(Piece.BlackPawn, result.pieceAt(Square.at("d3")));
        assertNull(result.pieceAt(Square.at("d4")));
        assertNull(result.pieceAt(Square.at("e4")));
    }

    @Test
    void enPassantTargetExpiresAfterAnotherMove() {
        Board board = new Board(
                Map.of(
                        Square.at("e5"), Piece.WhitePawn,
                        Square.at("d5"), Piece.BlackPawn,
                        Square.at("a1"), Piece.WhiteRook
                ),
                CastlingRights.initial(),
                Square.at("d6")
        );

        Board result = board.move(
                Square.at("a1"),
                Square.at("a2")
        );

        assertNull(result.enPassantTarget());
    }

    @Test
    void shouldRejectMoveThatLeavesKingInCheck() {
        Board board = new Board(Map.of(
                Square.at("e1"), Piece.WhiteKing,
                Square.at("e2"), Piece.WhiteRook,
                Square.at("e8"), Piece.BlackKing,
                Square.at("e7"), Piece.BlackRook
        ));

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(Square.at("e2"), Square.at("f2"))
        );
    }
}
