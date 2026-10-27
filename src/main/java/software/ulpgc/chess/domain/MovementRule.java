package software.ulpgc.chess.domain;

public interface MovementRule {

    boolean canMove(Board board, Square from, Square to);
}
