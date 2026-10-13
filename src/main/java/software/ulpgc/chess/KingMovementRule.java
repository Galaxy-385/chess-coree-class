
package software.ulpgc.chess;

public class KingMovementRule implements MovementRule {

    @Override
    public boolean canMove(Board board, Square from, Square to) {
        Piece piece = board.pieceAt(from);

        if (piece == null || piece.type() != PieceType.King) {
            return false;
        }

        if (from.equals(to)) {
            return false;
        }

        int fileDifference = Math.abs(
                to.file().getSymbol() - from.file().getSymbol()
        );

        int rankDifference = Math.abs(
                to.rank().getValue() - from.rank().getValue()
        );

        if (fileDifference > 1 || rankDifference > 1) {
            return false;
        }

        Piece destinationPiece = board.pieceAt(to);

        return destinationPiece == null ||
                destinationPiece.color() != piece.color();
    }
}
