
package software.ulpgc.chess;

import java.util.HashMap;
import java.util.Map;

import static software.ulpgc.chess.Piece.*;
import static software.ulpgc.chess.Square.at;

public class Board {

    private final Map<Square, Piece> pieces;

    public static Board InitialMap() {
        Map<Square, Piece> Initial = Map.ofEntries(

                // white pieces
                Map.entry(at("a1"), WhiteRook),
                Map.entry(at("b1"), WhiteKnight),
                Map.entry(at("c1"), WhiteBishop),
                Map.entry(at("d1"), WhiteQueen),
                Map.entry(at("e1"), WhiteKing),
                Map.entry(at("f1"), WhiteBishop),
                Map.entry(at("g1"), WhiteKnight),
                Map.entry(at("h1"), WhiteRook),

                // white pawns
                Map.entry(at("a2"), WhitePawn),
                Map.entry(at("b2"), WhitePawn),
                Map.entry(at("c2"), WhitePawn),
                Map.entry(at("d2"), WhitePawn),
                Map.entry(at("e2"), WhitePawn),
                Map.entry(at("f2"), WhitePawn),
                Map.entry(at("g2"), WhitePawn),
                Map.entry(at("h2"), WhitePawn),

                // black pieces
                Map.entry(at("a8"), BlackRook),
                Map.entry(at("b8"), BlackKnight),
                Map.entry(at("c8"), BlackBishop),
                Map.entry(at("d8"), BlackQueen),
                Map.entry(at("e8"), BlackKing),
                Map.entry(at("f8"), BlackBishop),
                Map.entry(at("g8"), BlackKnight),
                Map.entry(at("h8"), BlackRook),

                // black pawns
                Map.entry(at("a7"), BlackPawn),
                Map.entry(at("b7"), BlackPawn),
                Map.entry(at("c7"), BlackPawn),
                Map.entry(at("d7"), BlackPawn),
                Map.entry(at("e7"), BlackPawn),
                Map.entry(at("f7"), BlackPawn),
                Map.entry(at("g7"), BlackPawn),
                Map.entry(at("h7"), BlackPawn)
        );

        return new Board(Initial);
    }

    public static Board initial() {
        return InitialMap();
    }

    public Board move(Square from, Square to) {

        Piece piece = pieces.get(from);

        Map<Square, Piece> newPieces = new HashMap<>(pieces);

        newPieces.remove(from);
        newPieces.put(to, piece);

        return new Board(newPieces);
    }

    public Board(Map<Square, Piece> pieces) {
        this.pieces = Map.copyOf(pieces);
    }

    public Piece pieceAt(File file, Rank rank) {
        return pieceAt(new Square(file, rank));
    }

    public Piece pieceAt(Square square) {
        return pieces.get(square);
    }

    private Piece pieceAt(String string) {
        return pieceAt(Square.at(string));
    }

    public int pieceCount() {
        return pieces.size();
    }

    public Board legalMove(Square from, Square to) {
        return legalMove(from, to, null);
    }

    public Board legalMove(
            Square from, Square to, Piece promotionPiece) {

        MovementRules rules = new MovementRules();

        if (!rules.canMove(this, from, to)) {
            throw new IllegalArgumentException("Illegal move");
        }

        Piece movingPiece = pieceAt(from);

        boolean reachesLastRank =
                movingPiece.type() == PieceType.Pawn &&
                        ((movingPiece.color() == Color.White &&
                                to.rank() == Rank.R8) ||
                                (movingPiece.color() == Color.Black &&
                                        to.rank() == Rank.R1));

        if (reachesLastRank &&
                !isValidPromotionPiece(
                        promotionPiece, movingPiece.color())) {
            throw new IllegalArgumentException(
                    "A pawn must promote to a queen, rook, bishop or knight");
        }

        if (!reachesLastRank && promotionPiece != null) {
            throw new IllegalArgumentException(
                    "Promotion is only allowed on the last rank");
        }

        Board movedBoard = move(from, to);

        if (reachesLastRank) {
            Map<Square, Piece> updatedPieces =
                    new HashMap<>(movedBoard.pieces);

            updatedPieces.put(to, promotionPiece);

            return new Board(updatedPieces);
        }

        return movedBoard;
    }

    private boolean isValidPromotionPiece(
            Piece promotionPiece, Color pawnColor) {

        if (promotionPiece == null ||
                promotionPiece.color() != pawnColor) {
            return false;
        }

        PieceType type = promotionPiece.type();

        return type == PieceType.Queen ||
                type == PieceType.Rook ||
                type == PieceType.Bishop ||
                type == PieceType.Knight;
    }
}
