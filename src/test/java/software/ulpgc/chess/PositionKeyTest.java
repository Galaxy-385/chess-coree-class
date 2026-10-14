
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionKeyTest {

    @Test
    void shouldRecognizeSamePositionAndTurn() {
        Board firstBoard = Board.initial();
        Board secondBoard = Board.initial();

        PositionKey first = new PositionKey(firstBoard, Color.White);
        PositionKey second = new PositionKey(secondBoard, Color.White);

        assertEquals(first, second);
    }

    @Test
    void shouldDistinguishDifferentTurns() {
        Board board = Board.initial();

        PositionKey whiteTurn = new PositionKey(board, Color.White);
        PositionKey blackTurn = new PositionKey(board, Color.Black);

        assertNotEquals(whiteTurn, blackTurn);
    }
}
