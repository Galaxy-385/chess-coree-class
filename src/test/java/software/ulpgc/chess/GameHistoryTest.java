
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.*;

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

    @Test
    void shouldDetectThreefoldRepetitionDuringGame() {
        Game game = new Game();

        for (int i = 0; i < 2; i++) {
            game.move(Square.at("g1"), Square.at("f3"));
            game.move(Square.at("g8"), Square.at("f6"));
            game.move(Square.at("f3"), Square.at("g1"));
            game.move(Square.at("f6"), Square.at("g8"));
        }

        assertTrue(game.isThreefoldRepetition());
    }
}
