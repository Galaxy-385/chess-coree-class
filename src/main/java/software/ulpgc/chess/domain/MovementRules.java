
package software.ulpgc.chess.domain;

import java.util.List;

public class MovementRules {

    private final List<MovementRule> rules;

    public MovementRules() {
        this.rules = List.of(
                new PawnMovementRule(),
                new KnightMovementRule(),
                new RookMovementRule(),
                new BishopMovementRule(),
                new QueenMovementRule(),
                new KingMovementRule(),
                new CastlingMovementRule()
        );
    }

    public boolean canMove(Board board, Square from, Square to) {
        for (MovementRule rule : rules) {
            if (rule.canMove(board, from, to)) {
                return true;
            }
        }

        return false;
    }
}
