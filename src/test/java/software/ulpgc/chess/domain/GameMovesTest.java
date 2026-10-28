package software.ulpgc.chess.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameMovesTest {

    @Test
    void shouldRecordPlayedMoves() {
        Game game = new Game();

        game.move(Square.at("e2"), Square.at("e4"));
        game.move(Square.at("e7"), Square.at("e5"));

        assertEquals(2, game.moves().size());

        PlayedMove first = game.moves().get(0);
        assertEquals(1, first.moveNumber());
        assertEquals(Square.at("e2"), first.from());
        assertEquals(Square.at("e4"), first.to());
        assertNull(first.promotionPiece());

        PlayedMove second = game.moves().get(1);
        assertEquals(1, second.moveNumber());
        assertEquals(Square.at("e7"), second.from());
        assertEquals(Square.at("e5"), second.to());
    }
}