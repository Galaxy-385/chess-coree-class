package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PieceTypeTest {

    @Test
    void pawnIsPawn() {
        assertEquals(PieceType.Pawn, PieceType.Pawn);
    }

    @Test
    void rookIsRook() {
        assertEquals(PieceType.Rook, PieceType.Rook);
    }

    @Test
    void knightIsKnight() {
        assertEquals(PieceType.Knight, PieceType.Knight);
    }

    @Test
    void bishopIsBishop() {
        assertEquals(PieceType.Bishop, PieceType.Bishop);
    }

    @Test
    void queenIsQueen() {
        assertEquals(PieceType.Queen, PieceType.Queen);
    }

    @Test
    void kingIsKing() {
        assertEquals(PieceType.King, PieceType.King);
    }
}