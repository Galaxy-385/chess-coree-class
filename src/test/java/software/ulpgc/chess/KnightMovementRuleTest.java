
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KnightMovementRuleTest {

    private final KnightMovementRule rule = new KnightMovementRule();

    @Test
    void knightCanMoveInLShape() {
        assertTrue(rule.canMove(
                Board.initial(),
                Square.at("b1"),
                Square.at("c3")
        ));
    }

    @Test
    void knightCanJumpOverPieces() {
        assertTrue(rule.canMove(
                Board.initial(),
                Square.at("b1"),
                Square.at("a3")
        ));
    }

    @Test
    void knightCannotMoveLikeARook() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("b1"),
                Square.at("b3")
        ));
    }

    @Test
    void knightCannotMoveToSameSquare() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("b1"),
                Square.at("b1")
        ));
    }

    @Test
    void knightCannotCaptureOwnPiece() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("b1"),
                Square.at("d2")
        ));
    }

    @Test
    void emptySquareCannotMoveAsKnight() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("c3"),
                Square.at("d5")
        ));
    }
}
