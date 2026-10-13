
package software.ulpgc.chess;

public class KnightMovementRule implements MovementRule {

    @Override
    public boolean canMove(Board board, Square from, Square to) {
        Piece piece = board.pieceAt(from);

        if (piece == null || piece.type() != PieceType.Knight) {
            return false;
        }

        if (from.equals(to)) {
            return false;
        }

        int rankDifference = Math.abs(
                to.rank().getValue() - from.rank().getValue()
        );

        int fileDifference = Math.abs(
                to.file().getSymbol() - from.file().getSymbol()
        );

        if (!((rankDifference == 2 && fileDifference == 1) ||
                (rankDifference == 1 && fileDifference == 2))) {
            return false;
        }

        Piece destinationPiece = board.pieceAt(to);

        return destinationPiece == null ||
                destinationPiece.color() != piece.color();
    }
}
