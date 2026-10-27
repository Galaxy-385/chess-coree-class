
package software.ulpgc.chess.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static software.ulpgc.chess.domain.Piece.*;
import static software.ulpgc.chess.domain.Square.at;

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


        // Mover una torre desde su casilla inicial elimina
        // el derecho de enroque correspondiente.
        if (piece == Piece.WhiteRook) {
            if (from.equals(castlingRights.whiteQueenRookStart())) {
                newRights = newRights.withoutWhiteQueenSide();
            }

            if (from.equals(castlingRights.whiteKingRookStart())) {
                newRights = newRights.withoutWhiteKingSide();
            }
        }

        if (piece == Piece.BlackRook) {
            if (from.equals(castlingRights.blackQueenRookStart())) {
                newRights = newRights.withoutBlackQueenSide();
            }

            if (from.equals(castlingRights.blackKingRookStart())) {
                newRights = newRights.withoutBlackKingSide();
            }
        }

        // Capturar una torre en su casilla inicial también
        // elimina su derecho de enroque.
        if (capturedPiece == Piece.WhiteRook) {
            if (to.equals(castlingRights.whiteQueenRookStart())) {
                newRights = newRights.withoutWhiteQueenSide();
            }

            if (to.equals(castlingRights.whiteKingRookStart())) {
                newRights = newRights.withoutWhiteKingSide();
            }
        }

        if (capturedPiece == Piece.BlackRook) {
            if (to.equals(castlingRights.blackQueenRookStart())) {
                newRights = newRights.withoutBlackQueenSide();
            }

            if (to.equals(castlingRights.blackKingRookStart())) {
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

        Piece movingPiece = pieceAt(from);
        Piece targetPiece = pieceAt(to);

        boolean isCastling =
                (movingPiece == Piece.WhiteKing ||
                        movingPiece == Piece.BlackKing) &&
                        from.rank() == to.rank() &&
                        (to.file() == File.G ||
                                to.file() == File.C) &&
                        from.equals(
                                movingPiece == Piece.WhiteKing
                                        ? castlingRights.whiteKingStart()
                                        : castlingRights.blackKingStart()
                        );

        // Solo se permite que el destino contenga un rey
        // cuando se trata del caso especial de enroque.
        if ((targetPiece == Piece.WhiteKing ||
                targetPiece == Piece.BlackKing) &&
                !isCastling) {
            throw new IllegalArgumentException(
                    "Illegal move: Kings cannot be captured"
            );
        }

        if (!rules.canMove(this, from, to)) {
            throw new IllegalArgumentException("Illegal move");
        }


        if (isCastling) {
            boolean white = movingPiece == Piece.WhiteKing;
            boolean kingSide = to.file() == File.G;

            Square rookFrom;

            if (white) {
                rookFrom = kingSide
                        ? castlingRights.whiteKingRookStart()
                        : castlingRights.whiteQueenRookStart();
            } else {
                rookFrom = kingSide
                        ? castlingRights.blackKingRookStart()
                        : castlingRights.blackQueenRookStart();
            }

            Square rookTo = new Square(
                    kingSide ? File.F : File.D,
                    from.rank()
            );

            // Mover ambas piezas simultáneamente.
            Map<Square, Piece> updatedPieces =
                    new HashMap<>(pieces);

            updatedPieces.remove(from);
            updatedPieces.remove(rookFrom);

            updatedPieces.put(to, movingPiece);

            updatedPieces.put(
                    rookTo,
                    white ? Piece.WhiteRook : Piece.BlackRook
            );

            CastlingRights newRights = white
                    ? castlingRights.withoutWhiteCastling()
                    : castlingRights.withoutBlackCastling();

            Board castledBoard = new Board(
                    updatedPieces,
                    newRights,
                    null
            );

            CheckDetector checkDetector = new CheckDetector();

            if (checkDetector.isInCheck(
                    castledBoard, movingPiece.color())) {
                throw new IllegalArgumentException(
                        "Illegal move: king would be in check"
                );
            }

            return castledBoard;
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

            Board movedBoard = new Board(updatedPieces, castlingRights);

            CheckDetector checkDetector = new CheckDetector();

            if (checkDetector.isInCheck(movedBoard, movingPiece.color())) {
                throw new IllegalArgumentException(
                        "Illegal move: king would be in check"
                );
            }

            return movedBoard;
        }

        Board movedBoard = move(from, to);

        if (reachesLastRank) {
            Map<Square, Piece> updatedPieces =
                    new HashMap<>(movedBoard.pieces);

            updatedPieces.put(to, promotionPiece);

            movedBoard = new Board(
                    updatedPieces,
                    movedBoard.castlingRights,
                    movedBoard.enPassantTarget
            );
        }

        CheckDetector checkDetector = new CheckDetector();

        if (checkDetector.isInCheck(movedBoard, movingPiece.color())) {
            throw new IllegalArgumentException(
                    "Illegal move: king would be in check"
            );
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

    public static Board chess960() {
        Random random = new Random();
        Piece[] backRank = new Piece[8];

        // Los alfiles deben estar en colores opuestos.
        backRank[random.nextInt(4) * 2] = WhiteBishop;
        backRank[random.nextInt(4) * 2 + 1] = WhiteBishop;

        List<Integer> availableFiles = new ArrayList<>(
                List.of(0, 1, 2, 3, 4, 5, 6, 7)
        );

        // Reservar las columnas de los alfiles.
        for (int file = 0; file < 8; file++) {
            if (backRank[file] != null) {
                availableFiles.remove(Integer.valueOf(file));
            }
        }

        // Colocar la dama en una columna libre.
        int file = availableFiles.remove(random.nextInt(availableFiles.size()));
        backRank[file] = WhiteQueen;

        // Colocar los dos caballos.
        for (int i = 0; i < 2; i++) {
            file = availableFiles.remove(
                    random.nextInt(availableFiles.size())
            );
            backRank[file] = WhiteKnight;
        }

        // Las tres columnas restantes reciben torre, rey y torre.

        // Las tres columnas restantes reciben torre, rey y torre.
        availableFiles.sort(Integer::compareTo);

        int queenRookFile = availableFiles.get(0);
        int kingFile = availableFiles.get(1);
        int kingRookFile = availableFiles.get(2);

        backRank[queenRookFile] = WhiteRook;
        backRank[kingFile] = WhiteKing;
        backRank[kingRookFile] = WhiteRook;


        Map<Square, Piece> pieces = new HashMap<>();

        for (int i = 0; i < 8; i++) {
            String column = String.valueOf((char) ('a' + i));

            // Piezas blancas y peones.
            pieces.put(Square.at(column + "1"), backRank[i]);
            pieces.put(Square.at(column + "2"), WhitePawn);

            // Piezas negras con la misma disposición.
            Piece blackPiece = switch (backRank[i]) {
                case WhiteRook -> BlackRook;
                case WhiteKnight -> BlackKnight;
                case WhiteBishop -> BlackBishop;
                case WhiteQueen -> BlackQueen;
                case WhiteKing -> BlackKing;
                default -> throw new IllegalStateException(
                        "Unexpected piece: " + backRank[i]
                );
            };

            pieces.put(Square.at(column + "8"), blackPiece);
            pieces.put(Square.at(column + "7"), BlackPawn);
        }


        CastlingRights rights = new CastlingRights(
                true, true, true, true,
                Square.at(String.valueOf((char) ('a' + kingFile)) + "1"),
                Square.at(String.valueOf((char) ('a' + queenRookFile)) + "1"),
                Square.at(String.valueOf((char) ('a' + kingRookFile)) + "1"),
                Square.at(String.valueOf((char) ('a' + kingFile)) + "8"),
                Square.at(String.valueOf((char) ('a' + queenRookFile)) + "8"),
                Square.at(String.valueOf((char) ('a' + kingRookFile)) + "8")
        );

        return new Board(pieces, rights);

    }
}
