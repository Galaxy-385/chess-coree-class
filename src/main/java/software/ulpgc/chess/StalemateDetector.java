package software.ulpgc.chess;

public class StalemateDetector {

    public boolean isStalemate(Board board, Color color) {
        CheckDetector checkDetector = new CheckDetector();

        if (checkDetector.isInCheck(board, color)) {
            return false;
        }

        return !hasLegalMove(board, color);
    }

    private boolean hasLegalMove(Board board, Color color) {
        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {
                Square from = new Square(file, rank);
                Piece piece = board.pieceAt(from);

                if (piece == null || piece.color() != color) {
                    continue;
                }

                for (File targetFile : File.values()) {
                    for (Rank targetRank : Rank.values()) {
                        Square to = new Square(targetFile, targetRank);


                        try {
                            if (piece.type() == PieceType.Pawn
                                    && (to.rank() == Rank.R1 || to.rank() == Rank.R8)) {

                                Piece promotionPiece = piece.color() == Color.White
                                        ? Piece.WhiteQueen
                                        : Piece.BlackQueen;

                                board.legalMove(from, to, promotionPiece);
                                return true;

                            } else {
                                board.legalMove(from, to);
                                return true;
                            }

                        } catch (IllegalArgumentException exception) {
                            // el movimiento no es legal
                        }

                    }
                }
            }
        }

        return false;
    }

}