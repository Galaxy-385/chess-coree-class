
package software.ulpgc.chess.adapters.persistence;

import software.ulpgc.chess.domain.Board;
import software.ulpgc.chess.domain.CastlingRights;
import software.ulpgc.chess.domain.File;
import software.ulpgc.chess.domain.Piece;
import software.ulpgc.chess.domain.Rank;
import software.ulpgc.chess.domain.Square;

import java.util.HashMap;
import java.util.Map;

public class BoardStateMapper {

    public Map<String, String> toMap(Board board) {
        Map<String, String> result = new HashMap<>();

        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {
                Square square = new Square(file, rank);
                Piece piece = board.pieceAt(square);

                if (piece != null) {
                    result.put(
                            file.name().toLowerCase()
                                    + rank.name().substring(1),
                            piece.name()
                    );
                }
            }
        }

        return result;
    }

    public Board fromMap(
            Map<String, String> state,
            CastlingRights castlingRights,
            Square enPassantTarget) {

        Map<Square, Piece> pieces = new HashMap<>();

        for (Map.Entry<String, String> entry : state.entrySet()) {
            Square square = Square.at(entry.getKey());
            Piece piece = Piece.valueOf(entry.getValue());

            pieces.put(square, piece);
        }

        return new Board(pieces, castlingRights, enPassantTarget);
    }
}
