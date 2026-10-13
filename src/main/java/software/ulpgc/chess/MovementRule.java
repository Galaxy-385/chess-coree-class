package software.ulpgc.chess;

public interface MovementRule {

    boolean canMove(Board board, Square from, Square to);
}
