package software.ulpgc.chess;

import java.util.HashMap;
import java.util.Map;

import static software.ulpgc.chess.Piece.*;
import static software.ulpgc.chess.Square.at;


public class Board {

    private final Map<Square, Piece> pieces;

    public static Board InitialMap() {
        Map<Square, Piece> Initial = Map.ofEntries(

                // white pieces
                Map.entry(at("a1"), WhiteRook),
                Map.entry(at("b1"), WhiteKnight),
                Map.entry(at("c1"), WhiteBishop),
                Map.entry(at("d1"), WhiteQueen),
                Map.entry(at("e1"), WhiteKing),
                Map.entry(at("f1"), WhiteBishop),
                Map.entry(at("g1"), WhiteKnight),
                Map.entry(at("h1"), WhiteRook),

                // white pawns
                Map.entry(at("a2"), WhitePawn),
                Map.entry(at("b2"), WhitePawn),
                Map.entry(at("c2"), WhitePawn),
                Map.entry(at("d2"), WhitePawn),
                Map.entry(at("e2"), WhitePawn),
                Map.entry(at("f2"), WhitePawn),
                Map.entry(at("g2"), WhitePawn),
                Map.entry(at("h2"), WhitePawn),

                // black pieces
                Map.entry(at("a8"), BlackRook),
                Map.entry(at("b8"), BlackKnight),
                Map.entry(at("c8"), BlackBishop),
                Map.entry(at("d8"), BlackQueen),
                Map.entry(at("e8"), BlackKing),
                Map.entry(at("f8"), BlackBishop),
                Map.entry(at("g8"), BlackKnight),
                Map.entry(at("h8"), BlackRook),

                // black pawns
                Map.entry(at("a7"), BlackPawn),
                Map.entry(at("b7"), BlackPawn),
                Map.entry(at("c7"), BlackPawn),
                Map.entry(at("d7"), BlackPawn),
                Map.entry(at("e7"), BlackPawn),
                Map.entry(at("f7"), BlackPawn),
                Map.entry(at("g7"), BlackPawn),
                Map.entry(at("h7"), BlackPawn)
        );

        return new Board(Initial);
    }

    public static Board initial(){
        return InitialMap();
    }

    public Board move (Square from, Square to){

        Piece piece = pieces.get(from);

        Map<Square, Piece> newPieces = new HashMap<>(pieces);

        newPieces.remove(from);
        newPieces.put(to, piece);

        return new Board(newPieces);
    }

    public Board(Map<Square, Piece> pieces){this.pieces = pieces;}

    public Piece pieceAt(File file, Rank rank){
        return pieceAt(new Square(file, rank));
    }

    private Piece pieceAt(Square square){
        return pieces.get(square);
    }

    private Piece pieceAt(String string){
        return pieceAt(Square.at(string));
    }


}
