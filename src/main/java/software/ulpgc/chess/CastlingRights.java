
package software.ulpgc.chess;

public class CastlingRights {

    private final boolean whiteKingSide;
    private final boolean whiteQueenSide;
    private final boolean blackKingSide;
    private final boolean blackQueenSide;

    private final Square whiteKingStart;
    private final Square whiteQueenRookStart;
    private final Square whiteKingRookStart;

    private final Square blackKingStart;
    private final Square blackQueenRookStart;
    private final Square blackKingRookStart;

    // Constructor clásico: mantiene la compatibilidad con el código existente.
    public CastlingRights(
            boolean whiteKingSide,
            boolean whiteQueenSide,
            boolean blackKingSide,
            boolean blackQueenSide) {

        this(
                whiteKingSide,
                whiteQueenSide,
                blackKingSide,
                blackQueenSide,
                Square.at("e1"),
                Square.at("a1"),
                Square.at("h1"),
                Square.at("e8"),
                Square.at("a8"),
                Square.at("h8")
        );
    }

    // Constructor que permite indicar las posiciones iniciales en Chess960.
    public CastlingRights(
            boolean whiteKingSide,
            boolean whiteQueenSide,
            boolean blackKingSide,
            boolean blackQueenSide,
            Square whiteKingStart,
            Square whiteQueenRookStart,
            Square whiteKingRookStart,
            Square blackKingStart,
            Square blackQueenRookStart,
            Square blackKingRookStart) {

        this.whiteKingSide = whiteKingSide;
        this.whiteQueenSide = whiteQueenSide;
        this.blackKingSide = blackKingSide;
        this.blackQueenSide = blackQueenSide;

        this.whiteKingStart = whiteKingStart;
        this.whiteQueenRookStart = whiteQueenRookStart;
        this.whiteKingRookStart = whiteKingRookStart;

        this.blackKingStart = blackKingStart;
        this.blackQueenRookStart = blackQueenRookStart;
        this.blackKingRookStart = blackKingRookStart;
    }

    public static CastlingRights initial() {
        return new CastlingRights(true, true, true, true);
    }

    public boolean whiteKingSide() {
        return whiteKingSide;
    }

    public boolean whiteQueenSide() {
        return whiteQueenSide;
    }

    public boolean blackKingSide() {
        return blackKingSide;
    }

    public boolean blackQueenSide() {
        return blackQueenSide;
    }

    public Square whiteKingStart() {
        return whiteKingStart;
    }

    public Square whiteQueenRookStart() {
        return whiteQueenRookStart;
    }

    public Square whiteKingRookStart() {
        return whiteKingRookStart;
    }

    public Square blackKingStart() {
        return blackKingStart;
    }

    public Square blackQueenRookStart() {
        return blackQueenRookStart;
    }

    public Square blackKingRookStart() {
        return blackKingRookStart;
    }

    public CastlingRights withoutWhiteCastling() {
        return new CastlingRights(
                false, false, blackKingSide, blackQueenSide,
                whiteKingStart, whiteQueenRookStart, whiteKingRookStart,
                blackKingStart, blackQueenRookStart, blackKingRookStart
        );
    }

    public CastlingRights withoutBlackCastling() {
        return new CastlingRights(
                whiteKingSide, whiteQueenSide, false, false,
                whiteKingStart, whiteQueenRookStart, whiteKingRookStart,
                blackKingStart, blackQueenRookStart, blackKingRookStart
        );
    }

    public CastlingRights withoutWhiteKingSide() {
        return new CastlingRights(
                false, whiteQueenSide, blackKingSide, blackQueenSide,
                whiteKingStart, whiteQueenRookStart, whiteKingRookStart,
                blackKingStart, blackQueenRookStart, blackKingRookStart
        );
    }

    public CastlingRights withoutWhiteQueenSide() {
        return new CastlingRights(
                whiteKingSide, false, blackKingSide, blackQueenSide,
                whiteKingStart, whiteQueenRookStart, whiteKingRookStart,
                blackKingStart, blackQueenRookStart, blackKingRookStart
        );
    }

    public CastlingRights withoutBlackKingSide() {
        return new CastlingRights(
                whiteKingSide, whiteQueenSide, false, blackQueenSide,
                whiteKingStart, whiteQueenRookStart, whiteKingRookStart,
                blackKingStart, blackQueenRookStart, blackKingRookStart
        );
    }

    public CastlingRights withoutBlackQueenSide() {
        return new CastlingRights(
                whiteKingSide, whiteQueenSide, blackKingSide, false,
                whiteKingStart, whiteQueenRookStart, whiteKingRookStart,
                blackKingStart, blackQueenRookStart, blackKingRookStart
        );
    }
}
