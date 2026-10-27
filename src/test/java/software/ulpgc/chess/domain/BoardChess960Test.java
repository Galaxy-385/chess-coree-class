
package software.ulpgc.chess.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardChess960Test {

    @Test
    void chess960BoardHasCorrectNumberOfPieces() {
        Board board = Board.chess960();

        assertEquals(32, board.pieceCount());
    }

    @Test
    void chess960BishopsAreOnOppositeColors() {
        Board board = Board.chess960();

        int whiteBishopFile1 = -1;
        int whiteBishopFile2 = -1;

        for (int file = 0; file < 8; file++) {
            Piece piece = board.pieceAt(
                    new Square(File.values()[file], Rank.R1)
            );

            if (piece == Piece.WhiteBishop) {
                if (whiteBishopFile1 == -1) {
                    whiteBishopFile1 = file;
                } else {
                    whiteBishopFile2 = file;
                }
            }
        }

        assertNotEquals(-1, whiteBishopFile1);
        assertNotEquals(-1, whiteBishopFile2);
        assertNotEquals(
                whiteBishopFile1 % 2,
                whiteBishopFile2 % 2
        );
    }

    @Test
    void chess960KingIsBetweenTheRooks() {
        Board board = Board.chess960();

        int kingFile = -1;
        int rookFile1 = -1;
        int rookFile2 = -1;

        for (int file = 0; file < 8; file++) {
            Piece piece = board.pieceAt(
                    new Square(File.values()[file], Rank.R1)
            );

            if (piece == Piece.WhiteKing) {
                kingFile = file;
            }

            if (piece == Piece.WhiteRook) {
                if (rookFile1 == -1) {
                    rookFile1 = file;
                } else {
                    rookFile2 = file;
                }
            }
        }

        assertTrue(rookFile1 < kingFile);
        assertTrue(kingFile < rookFile2);
    }


    @Test
    void allGeneratedBoardsFollowChess960Rules() {
        for (int attempt = 0; attempt < 960; attempt++) {
            Board board = Board.chess960();

            int kingFile = -1;
            int leftRookFile = -1;
            int rightRookFile = -1;
            int bishopColor1 = -1;
            int bishopColor2 = -1;
            int bishopsFound = 0;
            int rooksFound = 0;

            for (int file = 0; file < 8; file++) {
                Piece piece = board.pieceAt(
                        new Square(File.values()[file], Rank.R1)
                );

                if (piece == Piece.WhiteKing) {
                    kingFile = file;
                }

                if (piece == Piece.WhiteRook) {
                    if (rooksFound == 0) {
                        leftRookFile = file;
                    } else {
                        rightRookFile = file;
                    }
                    rooksFound++;
                }

                if (piece == Piece.WhiteBishop) {
                    if (bishopsFound == 0) {
                        bishopColor1 = file % 2;
                    } else {
                        bishopColor2 = file % 2;
                    }
                    bishopsFound++;
                }
            }

            assertEquals(2, bishopsFound);
            assertEquals(2, rooksFound);
            assertTrue(leftRookFile < kingFile);
            assertTrue(kingFile < rightRookFile);
            assertNotEquals(bishopColor1, bishopColor2);
        }
    }


    @Test
    void chess960CanGenerate960DifferentStartingPositions() {
        java.util.Set<String> positions = new java.util.HashSet<>();

        for (int attempt = 0; attempt < 100_000 && positions.size() < 960; attempt++) {
            Board board = Board.chess960();
            StringBuilder position = new StringBuilder();

            for (int file = 0; file < 8; file++) {
                Piece piece = board.pieceAt(
                        new Square(File.values()[file], Rank.R1)
                );
                position.append(piece.name()).append("-");
            }

            positions.add(position.toString());
        }

        assertEquals(960, positions.size(),
                "No se han generado las 960 disposiciones distintas");
    }


}
