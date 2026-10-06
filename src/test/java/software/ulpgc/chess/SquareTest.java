package software.ulpgc.chess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SquareTest {

    @Test
    void createsSquareA1() {
        Square square = Square.at("a1");

        assertEquals(File.A, square.file());
        assertEquals(Rank.R1, square.rank());
    }

    @Test
    void createsSquareH8() {
        Square square = Square.at("h8");

        assertEquals(File.H, square.file());
        assertEquals(Rank.R8, square.rank());
    }

    @Test
    void createsSquareD4() {
        Square square = Square.at("d4");

        assertEquals(File.D, square.file());
        assertEquals(Rank.R4, square.rank());
    }

    @Test
    void createsSquareG6() {
        Square square = Square.at("g6");

        assertEquals(File.G, square.file());
        assertEquals(Rank.R6, square.rank());
    }

    @Test
    void createsSquareB7() {
        Square square = Square.at("b7");

        assertEquals(File.B, square.file());
        assertEquals(Rank.R7, square.rank());
    }

    @Test
    void createsSquareF3() {
        Square square = Square.at("f3");

        assertEquals(File.F, square.file());
        assertEquals(Rank.R3, square.rank());
    }
}