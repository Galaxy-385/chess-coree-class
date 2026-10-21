
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CastlingChess960Test {

    @Test
    void kingSideCastlingMovesKingAndRookToCorrectSquares() {
        Board board = createBoard(
                "e1", Piece.WhiteKing,
                "h1", Piece.WhiteRook,
                "e8", Piece.BlackKing
        );

        Board result = board.legalMove(
                Square.at("e1"),
                Square.at("g1")
        );

        assertEquals(Piece.WhiteKing, result.pieceAt(Square.at("g1")));
        assertEquals(Piece.WhiteRook, result.pieceAt(Square.at("f1")));
        assertNull(result.pieceAt(Square.at("e1")));
        assertNull(result.pieceAt(Square.at("h1")));
    }

    @Test
    void queenSideCastlingMovesKingAndRookToCorrectSquares() {
        Board board = createBoard(
                "e1", Piece.WhiteKing,
                "a1", Piece.WhiteRook,
                "e8", Piece.BlackKing
        );

        Board result = board.legalMove(
                Square.at("e1"),
                Square.at("c1")
        );

        assertEquals(Piece.WhiteKing, result.pieceAt(Square.at("c1")));
        assertEquals(Piece.WhiteRook, result.pieceAt(Square.at("d1")));
        assertNull(result.pieceAt(Square.at("e1")));
        assertNull(result.pieceAt(Square.at("a1")));
    }

    private Board createBoard(Object... entries) {
        Map<Square, Piece> pieces = new java.util.HashMap<>();

        for (int i = 0; i < entries.length; i += 2) {
            pieces.put(
                    Square.at((String) entries[i]),
                    (Piece) entries[i + 1]
            );
        }

        return new Board(pieces, CastlingRights.initial());
    }



    @Test
    void kingAlreadyOnCastlingDestinationCanCastle() {
        Map<Square, Piece> pieces = new java.util.HashMap<>();

        pieces.put(Square.at("g1"), Piece.WhiteKing);
        pieces.put(Square.at("h1"), Piece.WhiteRook);
        pieces.put(Square.at("e8"), Piece.BlackKing);

        CastlingRights rights = new CastlingRights(
                true, true, true, true,
                Square.at("g1"),
                Square.at("a1"),
                Square.at("h1"),
                Square.at("e8"),
                Square.at("a8"),
                Square.at("h8")
        );

        Board board = new Board(pieces, rights);

        Board result = board.legalMove(
                Square.at("g1"),
                Square.at("g1")
        );

        assertEquals(Piece.WhiteKing, result.pieceAt(Square.at("g1")));
        assertEquals(Piece.WhiteRook, result.pieceAt(Square.at("f1")));
        assertNull(result.pieceAt(Square.at("h1")));
    }



    @Test
    void rookAlreadyOnCastlingDestinationCanCastle() {
        Map<Square, Piece> pieces = new java.util.HashMap<>();

        pieces.put(Square.at("e1"), Piece.WhiteKing);
        pieces.put(Square.at("f1"), Piece.WhiteRook);
        pieces.put(Square.at("a1"), Piece.WhiteRook);
        pieces.put(Square.at("e8"), Piece.BlackKing);

        CastlingRights rights = new CastlingRights(
                true, true, true, true,
                Square.at("e1"),
                Square.at("a1"),
                Square.at("f1"),
                Square.at("e8"),
                Square.at("a8"),
                Square.at("h8")
        );

        Board board = new Board(pieces, rights);

        Board result = board.legalMove(
                Square.at("e1"),
                Square.at("g1")
        );

        assertEquals(Piece.WhiteKing, result.pieceAt(Square.at("g1")));
        assertEquals(Piece.WhiteRook, result.pieceAt(Square.at("f1")));
        assertNull(result.pieceAt(Square.at("e1")));
    }


    @Test
    void cannotCastleWhenKingIsInCheck() {
        Map<Square, Piece> pieces = new java.util.HashMap<>();

        pieces.put(Square.at("e1"), Piece.WhiteKing);
        pieces.put(Square.at("h1"), Piece.WhiteRook);
        pieces.put(Square.at("e8"), Piece.BlackKing);
        pieces.put(Square.at("e2"), Piece.BlackRook);

        Board board = new Board(pieces, CastlingRights.initial());

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(Square.at("e1"), Square.at("g1"))
        );
    }

    @Test
    void cannotCastleThroughAttackedSquare() {
        Map<Square, Piece> pieces = new java.util.HashMap<>();

        pieces.put(Square.at("e1"), Piece.WhiteKing);
        pieces.put(Square.at("h1"), Piece.WhiteRook);
        pieces.put(Square.at("a8"), Piece.BlackKing);
        pieces.put(Square.at("f8"), Piece.BlackRook);

        Board board = new Board(pieces, CastlingRights.initial());

        assertThrows(IllegalArgumentException.class, () ->
                board.legalMove(Square.at("e1"), Square.at("g1"))
        );
    }



}
