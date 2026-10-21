
package software.ulpgc.chess;

public class CastlingMovementRule implements MovementRule {

    @Override
    public boolean canMove(Board board, Square from, Square to) {

        Piece king = board.pieceAt(from);

        if (king != Piece.WhiteKing &&
                king != Piece.BlackKing) {
            return false;
        }

        CastlingRights rights = board.castlingRights();

        boolean white = king == Piece.WhiteKing;

        Square kingStart = white
                ? rights.whiteKingStart()
                : rights.blackKingStart();

        if (!from.equals(kingStart)) {
            return false;
        }

        int rank = from.rank().getValue();

        if (to.rank().getValue() != rank) {
            return false;
        }

        boolean kingSide;

        if (to.file() == File.G) {
            kingSide = true;
        } else if (to.file() == File.C) {
            kingSide = false;
        } else {
            return false;
        }

        if (white) {
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

        Square rookStart;

        if (white) {
            rookStart = kingSide
                    ? rights.whiteKingRookStart()
                    : rights.whiteQueenRookStart();
        } else {
            rookStart = kingSide
                    ? rights.blackKingRookStart()
                    : rights.blackQueenRookStart();
        }

        Piece expectedRook = white
                ? Piece.WhiteRook
                : Piece.BlackRook;

        if (board.pieceAt(rookStart) != expectedRook) {
            return false;
        }

        int kingDestinationFile = kingSide ? 6 : 2;
        int rookDestinationFile = kingSide ? 5 : 3;

        int rookStartFile =
                rookStart.file().getSymbol() - 'a';

        // Las casillas del recorrido del rey deben estar libres,
        // salvo las posiciones iniciales del rey y de la torre.
        for (int file = Math.min(
                from.file().getSymbol() - 'a',
                kingDestinationFile);
             file <= Math.max(
                     from.file().getSymbol() - 'a',
                     kingDestinationFile);
             file++) {

            Square square = new Square(
                    File.values()[file],
                    from.rank()
            );

            if (!square.equals(from) &&
                    !square.equals(rookStart) &&
                    board.pieceAt(square) != null) {
                return false;
            }
        }

        // Las casillas del recorrido de la torre también
        // deben estar libres, salvo las posiciones iniciales.
        for (int file = Math.min(
                rookStartFile,
                rookDestinationFile);
             file <= Math.max(
                     rookStartFile,
                     rookDestinationFile);
             file++) {

            Square square = new Square(
                    File.values()[file],
                    from.rank()
            );

            if (!square.equals(from) &&
                    !square.equals(rookStart) &&
                    board.pieceAt(square) != null) {
                return false;
            }
        }

        Color attackerColor = white
                ? Color.Black
                : Color.White;

        AttackDetector detector = new AttackDetector();

        if (detector.isSquareAttacked(
                board, from, attackerColor)) {
            return false;
        }

        // Comprobar las casillas que atraviesa el rey.
        int startFile = from.file().getSymbol() - 'a';
        int direction = Integer.compare(
                kingDestinationFile, startFile);

        if (direction != 0) {
            for (int file = startFile + direction;
                    ;
                 file += direction) {

                Square square = new Square(
                        File.values()[file],
                        from.rank()
                );

                if (detector.isSquareAttacked(
                        board, square, attackerColor)) {
                    return false;
                }

                if (file == kingDestinationFile) {
                    break;
                }
            }
        }

        return true;
    }
}
