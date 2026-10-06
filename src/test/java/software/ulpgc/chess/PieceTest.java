package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PieceTest {

    @Test
    void whitePawnHasCorrectColor() {
        assertEquals(Color.White, Piece.WhitePawn.color());
    }

    @Test
    void blackPawnHasCorrectColor() {
        assertEquals(Color.Black, Piece.BlackPawn.color());
    }

    @Test
    void whitePawnHasCorrectType() {
        assertEquals(PieceType.Pawn, Piece.WhitePawn.type());
    }

    @Test
    void blackKingHasCorrectType() {
        assertEquals(PieceType.King, Piece.BlackKing.type());
    }

    @Test
    void whiteQueenHasCorrectType() {
        assertEquals(PieceType.Queen, Piece.WhiteQueen.type());
    }

    @Test
    void blackKnightHasCorrectType() {
        assertEquals(PieceType.Knight, Piece.BlackKnight.type());
    }
}