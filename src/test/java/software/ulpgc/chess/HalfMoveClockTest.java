
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HalfMoveClockTest {

    @Test
    void counterStartsAtZero() {
        HalfMoveClock clock = new HalfMoveClock();

        assertEquals(0, clock.count());
    }

    @Test
    void quietMoveIncrementsCounter() {
        HalfMoveClock clock = new HalfMoveClock();

        clock.recordMove(Piece.WhiteKnight, null);

        assertEquals(1, clock.count());
    }

    @Test
    void consecutiveQuietMovesIncrementCounter() {
        HalfMoveClock clock = new HalfMoveClock();

        clock.recordMove(Piece.WhiteKnight, null);
        clock.recordMove(Piece.BlackBishop, null);
        clock.recordMove(Piece.WhiteRook, null);

        assertEquals(3, clock.count());
    }

    @Test
    void pawnMoveResetsCounter() {
        HalfMoveClock clock = new HalfMoveClock();

        clock.recordMove(Piece.WhiteKnight, null);
        clock.recordMove(Piece.BlackKnight, null);

        clock.recordMove(Piece.WhitePawn, null);

        assertEquals(0, clock.count());
    }

    @Test
    void captureResetsCounter() {
        HalfMoveClock clock = new HalfMoveClock();

        clock.recordMove(Piece.WhiteKnight, null);
        clock.recordMove(Piece.BlackBishop, null);

        clock.recordMove(Piece.WhiteRook, Piece.BlackKnight);

        assertEquals(0, clock.count());
    }

    @Test
    void isNotDrawBeforeOneHundredHalfMoves() {
        HalfMoveClock clock = new HalfMoveClock();

        for (int i = 0; i < 99; i++) {
            clock.recordMove(Piece.WhiteKnight, null);
        }

        assertFalse(clock.isDraw());
    }

    @Test
    void isDrawAfterOneHundredHalfMoves() {
        HalfMoveClock clock = new HalfMoveClock();

        for (int i = 0; i < 100; i++) {
            clock.recordMove(Piece.WhiteKnight, null);
        }

        assertTrue(clock.isDraw());
    }

    @Test
    void remainsDrawAfterOneHundredAndOneHalfMoves() {
        HalfMoveClock clock = new HalfMoveClock();

        for (int i = 0; i < 101; i++) {
            clock.recordMove(Piece.WhiteKnight, null);
        }

        assertTrue(clock.isDraw());
    }
}
