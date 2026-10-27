
package software.ulpgc.chess.domain;

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
