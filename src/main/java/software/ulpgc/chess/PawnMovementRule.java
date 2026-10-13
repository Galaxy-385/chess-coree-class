
package software.ulpgc.chess;

public class PawnMovementRule implements MovementRule {

    @Override
    public boolean canMove(Board board, Square from, Square to) {
        Piece piece = board.pieceAt(from);

        if (piece == null || piece.type() != PieceType.Pawn) {
            return false;
        }

        if (from.equals(to)) {
            return false;
        }

        int direction = piece.color() == Color.White ? 1 : -1;

        int rankDifference = to.rank().getValue() - from.rank().getValue();
        int fileDifference = to.file().getSymbol() - from.file().getSymbol();

        Piece destinationPiece = board.pieceAt(to);


        if (Math.abs(fileDifference) == 1 && rankDifference == direction) {
            return destinationPiece != null &&
                    destinationPiece.color() != piece.color();
        }

        if (fileDifference != 0) {
            return false;
        }

        if (rankDifference == direction) {
            return destinationPiece == null;
        }

        int initialRank = piece.color() == Color.White ? 2 : 7;


        if (from.rank().getValue() == initialRank &&
                rankDifference == 2 * direction) {

            Square middle = new Square(
                    from.file(),
                    Rank.values()[from.rank().ordinal() + direction]
            );

            return board.pieceAt(middle) == null &&
                    destinationPiece == null;
        }

        return false;
    }
}
