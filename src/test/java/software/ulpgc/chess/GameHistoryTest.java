
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameHistoryTest {

    @Test
    void shouldCountRecordedPositions() {
        GameHistory history = new GameHistory();
        Board board = Board.initial();

        history.recordPosition(board, Color.White);
        history.recordPosition(board, Color.White);
        history.recordPosition(board, Color.White);

        assertEquals(3, history.occurrences(board, Color.White));
    }

    @Test
    void shouldReturnZeroForUnrecordedPosition() {
        GameHistory history = new GameHistory();
        Board board = Board.initial();

        assertEquals(0, history.occurrences(board, Color.White));
    }

    @Test
    void shouldDetectThreefoldRepetition() {
        GameHistory history = new GameHistory();
        Board board = Board.initial();

        history.recordPosition(board, Color.White);
        history.recordPosition(board, Color.White);
        history.recordPosition(board, Color.White);

        assertTrue(history.isThreefoldRepetition(board, Color.White));
    }

    @Test
    void shouldNotDetectThreefoldRepetitionAfterTwoOccurrences() {
        GameHistory history = new GameHistory();
        Board board = Board.initial();

        history.recordPosition(board, Color.White);
        history.recordPosition(board, Color.White);

        assertFalse(history.isThreefoldRepetition(board, Color.White));
    }

    @Test
    void shouldDistinguishTheTurn() {
        GameHistory history = new GameHistory();
        Board board = Board.initial();

        history.recordPosition(board, Color.White);
        history.recordPosition(board, Color.Black);
        history.recordPosition(board, Color.White);

        assertEquals(2, history.occurrences(board, Color.White));
        assertEquals(1, history.occurrences(board, Color.Black));
    }
}
