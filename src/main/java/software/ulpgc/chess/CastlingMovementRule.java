
package software.ulpgc.chess;

public class CastlingMovementRule implements MovementRule {

    @Override
    public boolean canMove(Board board, Square from, Square to) {

        Piece king = board.pieceAt(from);

        if (king != Piece.WhiteKing &&
                king != Piece.BlackKing) {
            return false;
        }

        if (from.rank() != to.rank()) {
            return false;
        }

        int fileDifference =
                to.file().getSymbol() - from.file().getSymbol();

        if (Math.abs(fileDifference) != 2) {
            return false;
        }

        boolean kingSide = fileDifference > 0;
        CastlingRights rights = board.castlingRights();

        if (king == Piece.WhiteKing) {
            if (kingSide && !rights.whiteKingSide()) {
                return false;
            }

            if (!kingSide && !rights.whiteQueenSide()) {
                return false;
            }
        } else {
            if (kingSide && !rights.blackKingSide()) {
                return false;
            }

            if (!kingSide && !rights.blackQueenSide()) {
                return false;
            }
        }

        int rank = from.rank().getValue();

        Square rookSquare = new Square(
                kingSide ? File.H : File.A,
                from.rank()
        );

        Piece expectedRook = king == Piece.WhiteKing
                ? Piece.WhiteRook : Piece.BlackRook;

        if (board.pieceAt(rookSquare) != expectedRook) {
            return false;
        }

        int direction = kingSide ? 1 : -1;

        for (int file = from.file().getSymbol() + direction;
             file != rookSquare.file().getSymbol();
             file += direction) {

            Square square = new Square(
                    File.values()[file - 'a'],
                    from.rank()
            );

            if (board.pieceAt(square) != null) {
                return false;
            }
        }


        Color attackerColor =
                king.color() == Color.White ? Color.Black : Color.White;

        AttackDetector detector = new AttackDetector();

        Square middleSquare = new Square(
                File.values()[
                        from.file().getSymbol() - 'a' +
                                (kingSide ? 1 : -1)
                        ],
                from.rank()
        );

        if (detector.isSquareAttacked(board, from, attackerColor)) {
            return false;
        }

        if (detector.isSquareAttacked(
                board, middleSquare, attackerColor)) {
            return false;
        }

        if (detector.isSquareAttacked(board, to, attackerColor)) {
            return false;
        }


        return true;
    }
}
