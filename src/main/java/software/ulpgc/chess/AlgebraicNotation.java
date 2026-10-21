
package software.ulpgc.chess;

import java.util.ArrayList;
import java.util.List;

public class AlgebraicNotation {

    public static String beforeMove(
            Board board,
            Square from,
            Square to,
            Piece promotionPiece) {

        Piece piece = board.pieceAt(from);
        Piece target = board.pieceAt(to);

        boolean isPawn = piece.type() == PieceType.Pawn;

        boolean isEnPassant =
                isPawn
                        && to.equals(board.enPassantTarget())
                        && from.file() != to.file()
                        && target == null;

        boolean isCapture = target != null || isEnPassant;

        // castling
        if (piece.type() == PieceType.King
                && Math.abs(to.file().getSymbol()
                - from.file().getSymbol()) == 2) {

            return to.file().getSymbol() > from.file().getSymbol()
                    ? "O-O"
                    : "O-O-O";
        }

        StringBuilder notation = new StringBuilder();

        // piece letter (pawns have no letter)
        if (!isPawn) {
            notation.append(pieceLetter(piece));
            notation.append(disambiguation(board, piece, from, to));
        } else if (isCapture) {
            notation.append(from.file().getSymbol());
        }

        if (isCapture) {
            notation.append("x");
        }

        notation.append(squareName(to));

        // promotion
        if (promotionPiece != null) {
            notation.append("=");
            notation.append(pieceLetter(promotionPiece));
        }

        return notation.toString();
    }

    public static String withCheckSuffix(String notation, Game game) {
        if (game.isCheckmate()) {
            return notation + "#";
        }

        CheckDetector checkDetector = new CheckDetector();

        if (checkDetector.isInCheck(game.board(), game.turn())) {
            return notation + "+";
        }

        return notation;
    }

    private static String disambiguation(
            Board board,
            Piece piece,
            Square from,
            Square to) {

        List<Square> alternatives = new ArrayList<>();

        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {

                Square candidate = new Square(file, rank);

                if (candidate.equals(from)) {
                    continue;
                }

                Piece other = board.pieceAt(candidate);

                if (other == null
                        || other.color() != piece.color()
                        || other.type() != piece.type()) {
                    continue;
                }

                try {
                    board.legalMove(candidate, to);
                    alternatives.add(candidate);
                } catch (IllegalArgumentException exception) {
                    // this piece cannot legally reach the destination
                }
            }
        }

        if (alternatives.isEmpty()) {
            return "";
        }

        boolean sameFile = false;
        boolean sameRank = false;

        for (Square alternative : alternatives) {
            if (alternative.file() == from.file()) {
                sameFile = true;
            }

            if (alternative.rank() == from.rank()) {
                sameRank = true;
            }
        }

        if (!sameFile) {
            return String.valueOf(from.file().getSymbol());
        }

        if (!sameRank) {
            return String.valueOf(from.rank().getValue());
        }

        return String.valueOf(from.file().getSymbol())
                + from.rank().getValue();
    }

    private static String pieceLetter(Piece piece) {
        return switch (piece.type()) {
            case Knight -> "N";
            case Bishop -> "B";
            case Rook -> "R";
            case Queen -> "Q";
            case King -> "K";
            case Pawn -> "";
        };
    }

    private static String squareName(Square square) {
        return String.valueOf(square.file().getSymbol())
                + square.rank().getValue();
    }
}
