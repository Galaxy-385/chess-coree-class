package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {

    @Test
    void initialBoardHasWhitePawnAtE2(){
        Board board = Board.initial();

        assertEquals(
                Piece.WhitePawn,
                board.pieceAt(File.E, Rank.R2)
        );
    }
}
