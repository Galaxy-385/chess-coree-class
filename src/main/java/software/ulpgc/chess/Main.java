package software.ulpgc.chess;

public class Main {
    public static void main(String[] args){

        Square a1 = Square.at("a1");
        Square h8 = Square.at("h8");

        System.out.println(a1);
        System.out.println(h8);

        Board board = Board.initial();

        Board movedBoard = board.move(
                Square.at("e2"),
                Square.at("e4")
        );
    }
}
