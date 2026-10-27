
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.Board;
import software.ulpgc.chess.domain.Piece;
import software.ulpgc.chess.domain.Square;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BoardPromotionTest {

    @Test
    void whitePawnCanPromoteToQueen() {
        Board board = new Board(Map.of(
                Square.at("a7"), Piece.WhitePawn
        ));

        Board promotedBoard = board.legalMove(
                Square.at("a7"),
                Square.at("a8"),
                Piece.WhiteQueen
        );

        assertEquals(
                Piece.WhiteQueen,
                promotedBoard.pieceAt(Square.at("a8"))
        );

        assertNull(promotedBoard.pieceAt(Square.at("a7")));
    }

    @Test
    void promotionWithoutChoosingPieceIsRejected() {
        Board board = new Board(Map.of(
                Square.at("a7"), Piece.WhitePawn
        ));

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(
                        Square.at("a7"),
                        Square.at("a8")
                )
        );
    }

    @Test
    void pawnCannotPromoteToKing() {
        Board board = new Board(Map.of(
                Square.at("a7"), Piece.WhitePawn
        ));

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(
                        Square.at("a7"),
                        Square.at("a8"),
                        Piece.WhiteKing
                )
        );
    }

    @Test
    void pawnCannotPromoteToPieceOfOppositeColor() {
        Board board = new Board(Map.of(
                Square.at("a7"), Piece.WhitePawn
        ));

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(
                        Square.at("a7"),
                        Square.at("a8"),
                        Piece.BlackQueen
                )
        );
    }


    @Test
    void whitePawnCanPromoteToRook() {
        Board board = new Board(Map.of(
                Square.at("a7"), Piece.WhitePawn
        ));

        Board result = board.legalMove(
                Square.at("a7"),
                Square.at("a8"),
                Piece.WhiteRook
        );

        assertEquals(Piece.WhiteRook, result.pieceAt(Square.at("a8")));
    }

    @Test
    void blackPawnCanPromoteToQueen() {
        Board board = new Board(Map.of(
                Square.at("a2"), Piece.BlackPawn
        ));

        Board result = board.legalMove(
                Square.at("a2"),
                Square.at("a1"),
                Piece.BlackQueen
        );

        assertEquals(Piece.BlackQueen, result.pieceAt(Square.at("a1")));
        assertNull(result.pieceAt(Square.at("a2")));
    }

    @Test
    void pawnCannotPromoteToPawn() {
        Board board = new Board(Map.of(
                Square.at("a7"), Piece.WhitePawn
        ));

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(
                        Square.at("a7"),
                        Square.at("a8"),
                        Piece.WhitePawn
                )
        );
    }

    @Test
    void promotionIsRejectedBeforeLastRank() {
        Board board = new Board(Map.of(
                Square.at("a6"), Piece.WhitePawn
        ));

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(
                        Square.at("a6"),
                        Square.at("a7"),
                        Piece.WhiteQueen
                )
        );
    }
}
