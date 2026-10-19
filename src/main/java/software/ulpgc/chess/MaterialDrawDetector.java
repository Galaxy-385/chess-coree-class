
package software.ulpgc.chess;

public class MaterialDrawDetector {

    public boolean isInsufficientMaterial(Board board) {
        int bishops = 0;
        int knights = 0;
        int otherPieces = 0;
        int kings = 0;

        Square firstBishopSquare = null;
        Square secondBishopSquare = null;

        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {
                Square square = new Square(file, rank);
                Piece piece = board.pieceAt(square);

                if (piece == null) {
                    continue;
                }

                switch (piece.type()) {
                    case King -> kings++;

                    case Bishop -> {
                        bishops++;

                        if (firstBishopSquare == null) {
                            firstBishopSquare = square;
                        } else {
                            secondBishopSquare = square;
                        }
                    }

                    case Knight -> knights++;

                    default -> otherPieces++;
                }
            }
        }

        if (otherPieces > 0 || kings != 2) {
            return false;
        }

        int minorPieces = bishops + knights;

        if (minorPieces == 0) {
            return true;
        }

        if (minorPieces == 1) {
            return true;
        }

        if (bishops == 2 && knights == 0) {
            return isSameSquareColor(
                    firstBishopSquare,
                    secondBishopSquare
            );
        }

        return false;
    }

    private boolean isSameSquareColor(Square first, Square second) {
        int firstColor =
                first.file().ordinal() + first.rank().getValue();

        int secondColor =
                second.file().ordinal() + second.rank().getValue();

        return firstColor % 2 == secondColor % 2;
    }
}
