
package software.ulpgc.chess.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BishopMovementRuleTest {

    private final BishopMovementRule rule = new BishopMovementRule();


    @Test
    void bishopCanMoveDiagonally() {
        Board board = Board.initial()
                .move(Square.at("c2"), Square.at("c3"))
                .move(Square.at("d2"), Square.at("d3"))
                .move(Square.at("c1"), Square.at("b2"))
                .move(Square.at("c3"), Square.at("c4"))
                .move(Square.at("d3"), Square.at("a3"));

        assertTrue(rule.canMove(
                board,
                Square.at("b2"),
                Square.at("e5")
        ));
    }



    @Test
    void bishopCannotMoveHorizontally() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("c1"),
                Square.at("d1")
        ));
    }

    @Test
    void bishopCannotMoveVertically() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("c1"),
                Square.at("c3")
        ));
    }

    @Test
    void bishopCannotJumpOverPieces() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("c1"),
                Square.at("f4")
        ));
    }

    @Test
    void bishopCannotCaptureOwnPiece() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("c1"),
                Square.at("d2")
        ));
    }

    @Test
    void bishopCannotMoveToSameSquare() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("c1"),
                Square.at("c1")
        ));
    }

    @Test
    void emptySquareCannotMoveAsBishop() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("c3"),
                Square.at("e5")
        ));
    }
}
