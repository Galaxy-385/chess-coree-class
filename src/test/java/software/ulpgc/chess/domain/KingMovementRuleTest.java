
package software.ulpgc.chess.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KingMovementRuleTest {

    private final KingMovementRule rule = new KingMovementRule();

    @Test
    void kingCanMoveOneSquareDiagonally() {
        Board board = Board.initial()
                .move(Square.at("e2"), Square.at("e3"))
                .move(Square.at("e1"), Square.at("e2"));

        assertTrue(rule.canMove(
                board,
                Square.at("e2"),
                Square.at("d3")
        ));
    }

    @Test
    void kingCanMoveOneSquareHorizontally() {
        Board board = Board.initial()
                .move(Square.at("d2"), Square.at("d3"))
                .move(Square.at("e1"), Square.at("e2"));

        assertTrue(rule.canMove(
                board,
                Square.at("e2"),
                Square.at("d2")
        ));
    }


    @Test
    void kingCanMoveOneSquareVertically() {
        Board board = Board.initial()
                .move(Square.at("e2"), Square.at("e3"))
                .move(Square.at("e1"), Square.at("e2"))
                .move(Square.at("e3"), Square.at("a3"));

        assertTrue(rule.canMove(
                board,
                Square.at("e2"),
                Square.at("e3")
        ));
    }


    @Test
    void kingCannotMoveTwoSquares() {
        Board board = Board.initial()
                .move(Square.at("e2"), Square.at("e3"))
                .move(Square.at("e1"), Square.at("e2"));

        assertFalse(rule.canMove(
                board,
                Square.at("e2"),
                Square.at("e4")
        ));
    }

    @Test
    void kingCannotCaptureOwnPiece() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("e1"),
                Square.at("e2")
        ));
    }

    @Test
    void kingCannotMoveToSameSquare() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("e1"),
                Square.at("e1")
        ));
    }

    @Test
    void emptySquareCannotMoveAsKing() {
        assertFalse(rule.canMove(
                Board.initial(),
                Square.at("e4"),
                Square.at("e5")
        ));
    }
}
