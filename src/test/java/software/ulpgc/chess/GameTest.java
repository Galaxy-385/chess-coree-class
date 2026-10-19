package software.ulpgc.chess;

import java.util.Map;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

public class GameTest {

    @Test
    void shouldPromotePawnAndChangeTurn() {
        Map<Square, Piece> pieces = new HashMap<>();
        pieces.put(Square.at("e1"), Piece.WhiteKing);
        pieces.put(Square.at("e8"), Piece.BlackKing);
        pieces.put(Square.at("a7"), Piece.WhitePawn);

        Board board = new Board(pieces);
        Game game = new Game(board);
    }

    @Test
    void shouldRecordPositionAfterPromotion() {
        Map<Square, Piece> pieces = new HashMap<>();
        pieces.put(Square.at("e1"), Piece.WhiteKing);
        pieces.put(Square.at("e8"), Piece.BlackKing);
        pieces.put(Square.at("a7"), Piece.WhitePawn);

        Game game = new Game(new Board(pieces));

        game.move(
                Square.at("a7"),
                Square.at("a8"),
                Piece.WhiteQueen
        );

        assertEquals(Piece.WhiteQueen,
                game.board().pieceAt(Square.at("a8")));
        assertEquals(Color.Black, game.turn());
        assertTrue(game.isThreefoldRepetition() == false);
    }

    @Test
    void shouldNotChangeGameWhenMovingOpponentPiece() {
        Game game = new Game();

        assertThrows(
                IllegalArgumentException.class,
                () -> game.move(
                        Square.at("e7"),
                        Square.at("e5")
                )
        );

        assertEquals(Color.White, game.turn());
        assertEquals(
                Piece.BlackPawn,
                game.board().pieceAt(Square.at("e7"))
        );
    }

    @Test
    void shouldNotChangeGameWhenMakingIllegalMove() {
        Game game = new Game();

        assertThrows(
                IllegalArgumentException.class,
                () -> game.move(
                        Square.at("e2"),
                        Square.at("e5")
                )
        );

        assertEquals(Color.White, game.turn());
        assertEquals(
                Piece.WhitePawn,
                game.board().pieceAt(Square.at("e2"))
        );
        assertFalse(game.isThreefoldRepetition());
    }

    @Test
    void shouldAllowCustomInitialTurn() {
        Board board = Board.initial();

        Game game = new Game(board, Color.Black);

        assertEquals(Color.Black, game.turn());
        assertEquals(
                Piece.WhiteKing,
                game.board().pieceAt(Square.at("e1"))
        );
    }


    @Test
    void shouldAllowBlackToMoveWhenBlackStarts() {
        Game game = new Game(Board.initial(), Color.Black);

        game.move(
                Square.at("e7"),
                Square.at("e5")
        );

        assertEquals(Color.White, game.turn());
        assertEquals(
                Piece.BlackPawn,
                game.board().pieceAt(Square.at("e5"))
        );
        assertEquals(
                null,
                game.board().pieceAt(Square.at("e7"))
        );
    }


    @Test
    void shouldAlternateTurnsAfterEachMove() {
        Game game = new Game();

        game.move(
                Square.at("e2"),
                Square.at("e4")
        );

        assertEquals(Color.Black, game.turn());

        game.move(
                Square.at("e7"),
                Square.at("e5")
        );

        assertEquals(Color.White, game.turn());
    }
}
