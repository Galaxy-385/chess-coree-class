
package software.ulpgc.chess.domain;

public class RookMovementRule implements MovementRule {

    @Override
    public boolean canMove(Board board, Square from, Square to) {
        Piece piece = board.pieceAt(from);

        if (piece == null || piece.type() != PieceType.Rook) {
            return false;
        }

        if (from.equals(to)) {
            return false;
        }

        boolean sameFile = from.file() == to.file();
        boolean sameRank = from.rank() == to.rank();

        if (!sameFile && !sameRank) {
            return false;
        }

        Piece destinationPiece = board.pieceAt(to);

        if (destinationPiece != null &&
                destinationPiece.color() == piece.color()) {
            return false;
        }

        int fileDirection = Integer.compare(
                to.file().getSymbol() - from.file().getSymbol(), 0
        );

        int rankDirection = Integer.compare(
                to.rank().getValue() - from.rank().getValue(), 0
        );

        int file = from.file().getSymbol() - 'a';
        int rank = from.rank().getValue() - 1;

        file += fileDirection;
        rank += rankDirection;

        while (file != to.file().getSymbol() - 'a' ||
                rank != to.rank().getValue() - 1) {

            Square square = new Square(
                    File.values()[file],
                    Rank.values()[rank]
            );

            if (board.pieceAt(square) != null) {
                return false;
            }

            file += fileDirection;
            rank += rankDirection;
        }

        return true;
    }
}
