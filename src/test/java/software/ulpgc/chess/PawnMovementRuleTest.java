
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PawnMovementRuleTest {

    private final PawnMovementRule rule = new PawnMovementRule();

    @Test
    void whitePawnCanMoveForwardOneSquare() {
        assertTrue(rule.canMove(
                Board.initial(),
                Square.at("e2"),
                Square.at("e3")
        ));
    }

    @Test
    void blackPawnCanMoveForwardOneSquare() {
        assertTrue(rule.canMove(
                Board.initial(),
                Square.at("e7"),
                Square.at("e6")
        ));
    }

    @Test
    void pawnCannotMoveDiagonallyWithThisRule() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("e2"),
                Square.at("f3")
        ));
    }

    @Test
    void emptySquareCannotMoveAsPawn() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("e3"),
                Square.at("e4")
        ));
    }

    @Test
    void whitePawnCanMoveTwoSquaresInitially() {
        assertTrue(rule.canMove(
                Board.initial(),
                Square.at("e2"),
                Square.at("e4")
        ));
    }

    @Test
    void blackPawnCanMoveTwoSquaresInitially() {
        assertTrue(rule.canMove(
                Board.initial(),
                Square.at("e7"),
                Square.at("e5")
        ));
    }

    @Test
    void pawnCannotMoveTwoSquaresAfterLeavingInitialRank() {
        Board board = Board.initial()
                .move(Square.at("e2"), Square.at("e3"));

        assertFalse(rule.canMove(
                board,
                Square.at("e3"),
                Square.at("e5")
        ));
    }

    @Test
    void pawnCannotMoveForwardIntoOccupiedSquare() {
        Board board = Board.initial()
                .move(Square.at("e2"), Square.at("e3"));

        assertFalse(rule.canMove(
                board,
                Square.at("e7"),
                Square.at("e3")
        ));
    }

    @Test
    void pawnCannotJumpOverOccupiedSquare() {
        Board board = Board.initial()
                .move(Square.at("e7"), Square.at("e3"));

        assertFalse(rule.canMove(
                board,
                Square.at("e2"),
                Square.at("e4")
        ));
    }

    @Test
    void pawnCannotMoveToSameSquare() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("e2"),
                Square.at("e2")
        ));
    }


    @Test
    void whitePawnCanCaptureBlackPieceDiagonally() {
        Board board = Board.initial()
                .move(Square.at("e2"), Square.at("e4"))
                .move(Square.at("d7"), Square.at("d5"));

        assertTrue(rule.canMove(
                board,
                Square.at("e4"),
                Square.at("d5")
        ));
    }

    @Test
    void blackPawnCanCaptureWhitePieceDiagonally() {
        Board board = Board.initial()
                .move(Square.at("e2"), Square.at("e4"))
                .move(Square.at("d7"), Square.at("d5"));

        assertTrue(rule.canMove(
                board,
                Square.at("d5"),
                Square.at("e4")
        ));
    }

    @Test
    void pawnCannotCaptureDiagonallyOnEmptySquare() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("e2"),
                Square.at("d3")
        ));
    }

    @Test
    void pawnCannotCaptureOwnPieceDiagonally() {
        Board board = Board.initial()
                .move(Square.at("d2"), Square.at("d3"));

        assertFalse(rule.canMove(
                board,
                Square.at("e2"),
                Square.at("d3")
        ));
    }

}
