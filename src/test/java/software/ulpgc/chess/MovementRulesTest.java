
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.Board;
import software.ulpgc.chess.domain.MovementRules;
import software.ulpgc.chess.domain.Square;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MovementRulesTest {

    private final MovementRules rules = new MovementRules();

    @Test
    void acceptsValidKnightMove() {
        assertTrue(rules.canMove(
                Board.initial(),
                Square.at("b1"),
                Square.at("c3")
        ));
    }

    @Test
    void rejectsInvalidKnightMove() {
        assertFalse(rules.canMove(
                Board.initial(),
                Square.at("b1"),
                Square.at("b3")
        ));
    }

    @Test
    void acceptsPawnCapture() {
        Board board = Board.initial()
                .move(Square.at("e2"), Square.at("e4"))
                .move(Square.at("d7"), Square.at("d5"));

        assertTrue(rules.canMove(
                board,
                Square.at("e4"),
                Square.at("d5")
        ));
    }

    @Test
    void rejectsMoveFromEmptySquare() {
        assertFalse(rules.canMove(
                Board.initial(),
                Square.at("e4"),
                Square.at("e5")
        ));
    }
}
