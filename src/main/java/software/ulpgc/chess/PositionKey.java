
package software.ulpgc.chess;

import java.util.Map;
import java.util.Objects;

public record PositionKey(
        Map<Square, Piece> pieces,
        boolean whiteKingSide,
        boolean whiteQueenSide,
        boolean blackKingSide,
        boolean blackQueenSide,
        Square enPassantTarget,
        Color turn
) {

    public PositionKey(Board board, Color turn) {
        this(
                boardPieces(board),
                board.castlingRights().whiteKingSide(),
                board.castlingRights().whiteQueenSide(),
                board.castlingRights().blackKingSide(),
                board.castlingRights().blackQueenSide(),
                board.enPassantTarget(),
                turn
        );
    }

    private static Map<Square, Piece> boardPieces(Board board) {
        Map<Square, Piece> pieces = new java.util.HashMap<>();

        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {
                Square square = new Square(file, rank);
                Piece piece = board.pieceAt(square);

                if (piece != null) {
                    pieces.put(square, piece);
                }
            }
        }

        return Map.copyOf(pieces);
    }
}
