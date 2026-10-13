
package software.ulpgc.chess;

import java.util.HashMap;
import java.util.Map;

import static software.ulpgc.chess.Piece.*;
import static software.ulpgc.chess.Square.at;

public class Board {

    private final Map<Square, Piece> pieces;
    private final CastlingRights castlingRights;
    private final Square enPassantTarget;

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
        Piece capturedPiece = pieces.get(to);

        Map<Square, Piece> newPieces = new HashMap<>(pieces);

        newPieces.remove(from);
        newPieces.put(to, piece);

        CastlingRights newRights = castlingRights;

        // moving a king removes both castling rights
        if (piece == Piece.WhiteKing) {
            newRights = newRights.withoutWhiteCastling();
        }

        if (piece == Piece.BlackKing) {
            newRights = newRights.withoutBlackCastling();
        }

        // moving a rook removes its corresponding castling right
        if (piece == Piece.WhiteRook) {
            if (from.equals(Square.at("a1"))) {
                newRights = newRights.withoutWhiteQueenSide();
            }

            if (from.equals(Square.at("h1"))) {
                newRights = newRights.withoutWhiteKingSide();
            }
        }

        if (piece == Piece.BlackRook) {
            if (from.equals(Square.at("a8"))) {
                newRights = newRights.withoutBlackQueenSide();
            }

            if (from.equals(Square.at("h8"))) {
                newRights = newRights.withoutBlackKingSide();
            }
        }

        // capturing a rook on its initial square removes its castling right
        if (capturedPiece == Piece.WhiteRook) {
            if (to.equals(Square.at("a1"))) {
                newRights = newRights.withoutWhiteQueenSide();
            }

            if (to.equals(Square.at("h1"))) {
                newRights = newRights.withoutWhiteKingSide();
            }
        }

        if (capturedPiece == Piece.BlackRook) {
            if (to.equals(Square.at("a8"))) {
                newRights = newRights.withoutBlackQueenSide();
            }

            if (to.equals(Square.at("h8"))) {
                newRights = newRights.withoutBlackKingSide();
            }
        }

        Square newEnPassantTarget = null;

        if (piece != null &&
                piece.type() == PieceType.Pawn &&
                Math.abs(to.rank().getValue() -
                        from.rank().getValue()) == 2) {

            int middleRank =
                    (from.rank().getValue() +
                            to.rank().getValue()) / 2;

            newEnPassantTarget = new Square(
                    from.file(),
                    Rank.values()[middleRank - 1]
            );
        }

        return new Board(
                newPieces,
                newRights,
                newEnPassantTarget
        );
    }



    public Board(Map<Square, Piece> pieces) {
        this(pieces, CastlingRights.initial(), null);
    }

    public Board(
            Map<Square, Piece> pieces,
            CastlingRights castlingRights) {

        this(pieces, castlingRights, null);
    }

    public Board(
            Map<Square, Piece> pieces,
            CastlingRights castlingRights,
            Square enPassantTarget) {

        this.pieces = Map.copyOf(pieces);
        this.castlingRights = castlingRights;
        this.enPassantTarget = enPassantTarget;
    }

    public Square enPassantTarget() {
        return enPassantTarget;
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

    public Board legalMove(Square from, Square to, Piece promotionPiece) {

        MovementRules rules = new MovementRules();

        if (!rules.canMove(this, from, to)) {
            throw new IllegalArgumentException("Illegal move");
        }

        Piece movingPiece = pieceAt(from);

        boolean isCastling =
                (movingPiece == Piece.WhiteKing ||
                        movingPiece == Piece.BlackKing) &&
                        from.rank() == to.rank() &&
                        Math.abs(to.file().getSymbol() -
                                from.file().getSymbol()) == 2;

        if (isCastling) {
            int rank = from.rank().getValue();
            boolean kingSide =
                    to.file().getSymbol() > from.file().getSymbol();

            Square rookFrom = Square.at(
                    kingSide
                            ? (rank == 1 ? "h1" : "h8")
                            : (rank == 1 ? "a1" : "a8")
            );

            Square rookTo = Square.at(
                    kingSide
                            ? (rank == 1 ? "f1" : "f8")
                            : (rank == 1 ? "d1" : "d8")
            );

            Board kingMoved = move(from, to);
            return kingMoved.move(rookFrom, rookTo);
        }

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

        // en passant capture
        boolean isEnPassant =
                movingPiece.type() == PieceType.Pawn &&
                        to.equals(enPassantTarget) &&
                        from.file() != to.file() &&
                        pieceAt(to) == null;

        if (isEnPassant) {
            Map<Square, Piece> updatedPieces =
                    new HashMap<>(pieces);

            updatedPieces.remove(from);

            Square capturedPawnSquare = new Square(
                    to.file(),
                    from.rank()
            );

            updatedPieces.remove(capturedPawnSquare);
            updatedPieces.put(to, movingPiece);

            return new Board(
                    updatedPieces,
                    castlingRights
            );
        }

        Board movedBoard = move(from, to);

        if (reachesLastRank) {
            Map<Square, Piece> updatedPieces =
                    new HashMap<>(movedBoard.pieces);

            updatedPieces.put(to, promotionPiece);

            return new Board(updatedPieces, movedBoard.castlingRights);
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

    public CastlingRights castlingRights() {
        return castlingRights;
    }

}
