
package software.ulpgc.chess;

public class AttackDetector {

    public boolean isSquareAttacked(
            Board board, Square square, Color attackerColor) {

        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {

                Square from = new Square(file, rank);
                Piece piece = board.pieceAt(from);

                if (piece == null || piece.color() != attackerColor) {
                    continue;
                }

                if (attacksSquare(board, from, square, piece)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean attacksSquare(
            Board board, Square from, Square to, Piece piece) {

        int fileDifference =
                to.file().getSymbol() - from.file().getSymbol();

        int rankDifference =
                to.rank().getValue() - from.rank().getValue();

        switch (piece.type()) {
            case Pawn:
                int direction =
                        piece.color() == Color.White ? 1 : -1;

                return Math.abs(fileDifference) == 1 &&
                        rankDifference == direction;

            case Knight:
                return Math.abs(fileDifference) == 1 &&
                        Math.abs(rankDifference) == 2 ||
                        Math.abs(fileDifference) == 2 &&
                                Math.abs(rankDifference) == 1;

            case King:
                return Math.abs(fileDifference) <= 1 &&
                        Math.abs(rankDifference) <= 1 &&
                        (fileDifference != 0 || rankDifference != 0);

            case Rook:
                if (fileDifference != 0 && rankDifference != 0) {
                    return false;
                }
                break;

            case Bishop:
                if (Math.abs(fileDifference) !=
                        Math.abs(rankDifference)) {
                    return false;
                }
                break;

            case Queen:
                if (fileDifference != 0 &&
                        rankDifference != 0 &&
                        Math.abs(fileDifference) !=
                                Math.abs(rankDifference)) {
                    return false;
                }
                break;
        }

        return pathIsClear(board, from, to);
    }

    private boolean pathIsClear(Board board, Square from, Square to) {

        if (from.equals(to)) {
            return true;
        }

        int fileStep = Integer.compare(
                to.file().getSymbol(),
                from.file().getSymbol()
        );

        int rankStep = Integer.compare(
                to.rank().getValue(),
                from.rank().getValue()
        );

        int file = from.file().getSymbol() + fileStep;
        int rank = from.rank().getValue() + rankStep;

        while (file != to.file().getSymbol() ||
                rank != to.rank().getValue()) {

            Square square = new Square(
                    File.values()[file - 'a'],
                    Rank.values()[rank - 1]
            );

            if (board.pieceAt(square) != null) {
                return false;
            }

            file += fileStep;
            rank += rankStep;
        }

        return true;
    }
}
