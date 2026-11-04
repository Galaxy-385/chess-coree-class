
package software.ulpgc.chess.domain.rules.detection;

import software.ulpgc.chess.domain.model.Board;
import software.ulpgc.chess.domain.model.Color;
import software.ulpgc.chess.domain.model.Square;

public class CheckDetector {

    public boolean isInCheck(Board board, Color color) {

        KingDetector kingDetector = new KingDetector();

        Square kingSquare = kingDetector.findKing(board, color);

        if (kingSquare == null) {
            return false;
        }

        Color attackerColor =
                color == Color.White ? Color.Black : Color.White;

        AttackDetector attackDetector = new AttackDetector();

        return attackDetector.isSquareAttacked(
                board,
                kingSquare,
                attackerColor
        );
    }
}
