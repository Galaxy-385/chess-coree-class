
package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import java.util.HashMap;

class PositionKeyTest {

    @Test
    void shouldRecognizeSamePositionAndTurn() {
        Board firstBoard = Board.initial();
        Board secondBoard = Board.initial();

        PositionKey first = new PositionKey(firstBoard, Color.White);
        PositionKey second = new PositionKey(secondBoard, Color.White);

        assertEquals(first, second);
    }

    @Test
    void shouldDistinguishDifferentTurns() {
        Board board = Board.initial();

        PositionKey whiteTurn = new PositionKey(board, Color.White);
        PositionKey blackTurn = new PositionKey(board, Color.Black);

        assertNotEquals(whiteTurn, blackTurn);
    }

    @Test
    void shouldDistinguishDifferentCastlingRights() {
        Board firstBoard = Board.initial();

        CastlingRights rights = new CastlingRights(
                false, true, true, true
        );

        Board secondBoard = new Board(
                getInitialPieces(),
                rights
        );

        PositionKey first = new PositionKey(firstBoard, Color.White);
        PositionKey second = new PositionKey(secondBoard, Color.White);

        assertNotEquals(first, second);
    }

    private Map<Square, Piece> getInitialPieces() {
        Map<Square, Piece> pieces = new HashMap<>();

        Board initialBoard = Board.initial();

        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {
                Square square = new Square(file, rank);
                Piece piece = initialBoard.pieceAt(square);

                if (piece != null) {
                    pieces.put(square, piece);
                }
            }
        }

        return pieces;
    }


    @Test
    void shouldDistinguishDifferentEnPassantTargets() {
        Board firstBoard = new Board(
                getInitialPieces(),
                CastlingRights.initial(),
                Square.at("e3")
        );

        Board secondBoard = new Board(
                getInitialPieces(),
                CastlingRights.initial(),
                null
        );

        PositionKey first = new PositionKey(firstBoard, Color.White);
        PositionKey second = new PositionKey(secondBoard, Color.White);

        assertNotEquals(first, second);
    }
}
