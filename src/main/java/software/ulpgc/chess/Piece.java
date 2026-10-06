package software.ulpgc.chess;

public enum Piece {

    WhitePawn,
    WhiteRook,
    WhiteQueen,
    WhiteKnight,
    WhiteBishop,
    WhiteKing,
    BlackPawn,
    BlackRook,
    BlackQueen,
    BlackKnight,
    BlackBishop,
    BlackKing;

    Color color(){
        return isWhite() ? Color.White : Color.Black;
    }

    Boolean isPawn(){
        return this == WhitePawn || this == BlackPawn;
    }

    boolean isKnight(){
        return this == WhiteKnight || this == BlackKnight;
    }

    boolean isBishop(){
        return this == WhiteBishop || this == BlackBishop;
    }

    boolean isQueen(){
        return this == WhiteQueen || this == BlackQueen;
    }

    boolean isRook(){
        return this == WhiteRook || this == BlackRook;
    }

    boolean isKing(){
        return this == WhiteKing || this == BlackKing;
    }


    private Boolean isWhite(){
        return this == WhitePawn
                || this == WhiteBishop
                || this == WhiteKing
                || this == WhiteRook
                || this == WhiteKnight
                || this == WhiteQueen;
    }
}
