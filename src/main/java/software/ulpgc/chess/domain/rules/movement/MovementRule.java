package software.ulpgc.chess.domain.rules.movement;

import software.ulpgc.chess.domain.model.Board;
import software.ulpgc.chess.domain.model.Square;

public interface MovementRule {

    boolean canMove(Board board, Square from, Square to);
}
