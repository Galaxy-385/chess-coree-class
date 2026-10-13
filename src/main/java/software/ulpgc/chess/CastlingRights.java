
package software.ulpgc.chess;

public class CastlingRights {

    private final boolean whiteKingSide;
    private final boolean whiteQueenSide;
    private final boolean blackKingSide;
    private final boolean blackQueenSide;

    public CastlingRights(
            boolean whiteKingSide,
            boolean whiteQueenSide,
            boolean blackKingSide,
            boolean blackQueenSide) {

        this.whiteKingSide = whiteKingSide;
        this.whiteQueenSide = whiteQueenSide;
        this.blackKingSide = blackKingSide;
        this.blackQueenSide = blackQueenSide;
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

    public CastlingRights withoutWhiteCastling() {
        return new CastlingRights(false, false,
                blackKingSide, blackQueenSide);
    }

    public CastlingRights withoutBlackCastling() {
        return new CastlingRights(whiteKingSide, whiteQueenSide,
                false, false);
    }

    public CastlingRights withoutWhiteKingSide() {
        return new CastlingRights(false, whiteQueenSide,
                blackKingSide, blackQueenSide);
    }

    public CastlingRights withoutWhiteQueenSide() {
        return new CastlingRights(whiteKingSide, false,
                blackKingSide, blackQueenSide);
    }

    public CastlingRights withoutBlackKingSide() {
        return new CastlingRights(whiteKingSide, whiteQueenSide,
                false, blackQueenSide);
    }

    public CastlingRights withoutBlackQueenSide() {
        return new CastlingRights(whiteKingSide, whiteQueenSide,
                blackKingSide, false);
    }
}
