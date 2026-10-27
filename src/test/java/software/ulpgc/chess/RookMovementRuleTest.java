
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.Board;
import software.ulpgc.chess.domain.RookMovementRule;
import software.ulpgc.chess.domain.Square;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RookMovementRuleTest {

    private final RookMovementRule rule = new RookMovementRule();


    @Test
    void rookCanMoveHorizontally() {
        Board board = Board.initial()
                .move(Square.at("a2"), Square.at("a4"))
                .move(Square.at("a1"), Square.at("a3"))
                .move(Square.at("a4"), Square.at("b4"));

        assertTrue(rule.canMove(
                board,
                Square.at("a3"),
                Square.at("a5")
        ));
    }


    @Test
    void rookCanMoveVertically() {
        Board board = Board.initial()
                .move(Square.at("a2"), Square.at("a4"))
                .move(Square.at("a1"), Square.at("a3"))
                .move(Square.at("a4"), Square.at("b4"));

        assertTrue(rule.canMove(
                board,
                Square.at("a3"),
                Square.at("a6")
        ));
    }

    @Test
    void rookCannotMoveDiagonally() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("a1"),
                Square.at("b2")
        ));
    }

    @Test
    void rookCannotJumpOverPieces() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("a1"),
                Square.at("a4")
        ));
    }

    @Test
    void rookCannotCaptureOwnPiece() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("a1"),
                Square.at("a2")
        ));
    }

    @Test
    void rookCannotMoveToSameSquare() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("a1"),
                Square.at("a1")
        ));
    }

    @Test
    void emptySquareCannotMoveAsRook() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("a3"),
                Square.at("a5")
        ));
    }
}
