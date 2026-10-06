package software.ulpgc.chess;

import static software.ulpgc.chess.File.*;
import static software.ulpgc.chess.Rank.*;

public class Main {
    static void main(){
        Square a = new Square(A, R1);
        Square b = new Square(A, R1);
        Square c = b;

        System.out.println(a == b);
        System.out.println(a.equals(b));
        System.out.println(b == c);
    }
}
