
package software.ulpgc.chess.domain;

public class KingDetector {

    public Square findKing(Board board, Color color) {

        Piece king = color == Color.White
                ? Piece.WhiteKing
                : Piece.BlackKing;

        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {

                Square square = new Square(file, rank);

                if (board.pieceAt(square) == king) {
                    return square;
                }
            }
        }

        return null;
    }
}
