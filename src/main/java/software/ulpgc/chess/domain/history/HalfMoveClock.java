
package software.ulpgc.chess.domain.history;

import software.ulpgc.chess.domain.model.Piece;
import software.ulpgc.chess.domain.model.PieceType;

public class HalfMoveClock {

    private int count;

    public HalfMoveClock() {
        this.count = 0;
    }

    public void recordMove(Piece piece, Piece capturedPiece) {
        if (piece.type() == PieceType.Pawn || capturedPiece != null) {
            count = 0;
        } else {
            count++;
        }
    }

    public int count() {
        return count;
    }

    public boolean isDraw() {
        return count >= 100;
    }
}
