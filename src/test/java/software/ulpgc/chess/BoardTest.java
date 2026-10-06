package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BoardTest {

    @Test
    void initialBoardHasWhitePawnAtE2(){
        Board board = Board.initial();

        assertEquals(
                Piece.WhitePawn,
                board.pieceAt(File.E, Rank.R2)
        );
    }

    @Test
    void initialBoardHasWhiteKingAtE1() {
        Board board = Board.initial();

        assertEquals(
                Piece.WhiteKing,
                board.pieceAt(File.E, Rank.R1)
        );
    }

    @Test
    void initialBoardHasBlackKingAtE8() {
        Board board = Board.initial();

        assertEquals(
                Piece.BlackKing,
                board.pieceAt(File.E, Rank.R8)
        );
    }

    @Test
    void initialBoardHasWhiteRookAtA1() {
        Board board = Board.initial();

        assertEquals(
                Piece.WhiteRook,
                board.pieceAt(File.A, Rank.R1)
        );
    }

    @Test
    void moveWhitePawnFromE2ToE4() {
        Board board = Board.initial();

        Board movedBoard = board.move(
                Square.at("e2"),
                Square.at("e4")
        );

        assertNull(
                movedBoard.pieceAt(File.E, Rank.R2)
        );

        assertEquals(
                Piece.WhitePawn,
                movedBoard.pieceAt(File.E, Rank.R4)
        );
    }

    @Test
    void movingAPieceDoesNotChangeOriginalBoard() {
        Board board = Board.initial();

        Board movedBoard = board.move(
                Square.at("e2"),
                Square.at("e4")
        );

        assertEquals(
                Piece.WhitePawn,
                board.pieceAt(File.E, Rank.R2)
        );

        assertNull(
                board.pieceAt(File.E, Rank.R4)
        );
    }

    @Test
    void moveReplacesPieceAtDestination() {
        Board board = Board.initial();

        Board movedBoard = board.move(
                Square.at("e2"),
                Square.at("e7")
        );

        assertEquals(
                Piece.WhitePawn,
                movedBoard.pieceAt(File.E, Rank.R7)
        );
    }

    @Test
    void moveBlackPawnFromE7ToE5() {
        Board board = Board.initial();

        Board movedBoard = board.move(
                Square.at("e7"),
                Square.at("e5")
        );

        assertNull(
                movedBoard.pieceAt(File.E, Rank.R7)
        );

        assertEquals(
                Piece.BlackPawn,
                movedBoard.pieceAt(File.E, Rank.R5)
        );
    }

    @Test
    void initialBoardHas32Pieces(){
        Board board = Board.initial();

        assertEquals(32, board.pieceCount());
    }


}
