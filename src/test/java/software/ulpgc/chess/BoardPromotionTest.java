
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

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
}
