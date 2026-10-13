
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueenMovementRuleTest {

    private final QueenMovementRule rule = new QueenMovementRule();

    @Test
    void queenCanMoveHorizontally() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("d1"),
                Square.at("d3")
        ));
    }

    @Test
    void queenCanMoveVertically() {
        Board board = Board.initial()
                .move(Square.at("d2"), Square.at("d3"))
                .move(Square.at("d1"), Square.at("d2"))
                .move(Square.at("d3"), Square.at("a3"));

        assertTrue(rule.canMove(
                board,
                Square.at("d2"),
                Square.at("d5")
        ));
    }


    @Test
    void queenCanMoveDiagonally() {
        Board board = Board.initial()
                .move(Square.at("d2"), Square.at("d3"))
                .move(Square.at("e2"), Square.at("e3"))
                .move(Square.at("d1"), Square.at("c2"))
                .move(Square.at("d3"), Square.at("a3"))
                .move(Square.at("e3"), Square.at("e4"))
                .move(Square.at("e4"), Square.at("e5"));

        assertTrue(rule.canMove(
                board,
                Square.at("c2"),
                Square.at("f5")
        ));
    }


    @Test
    void queenCannotMoveLikeAKnight() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("d1"),
                Square.at("e3")
        ));
    }

    @Test
    void queenCannotJumpOverPieces() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("d1"),
                Square.at("d4")
        ));
    }

    @Test
    void queenCannotCaptureOwnPiece() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("d1"),
                Square.at("d2")
        ));
    }

    @Test
    void queenCannotMoveToSameSquare() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("d1"),
                Square.at("d1")
        ));
    }

    @Test
    void emptySquareCannotMoveAsQueen() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("d4"),
                Square.at("d5")
        ));
    }
}
