package software.ulpgc.chess;

public enum Piece {

    WhitePawn(Color.White, PieceType.Pawn),
    WhiteRook(Color.White, PieceType.Rook),
    WhiteQueen(Color.White, PieceType.Queen),
    WhiteKnight(Color.White, PieceType.Knight),
    WhiteBishop(Color.White, PieceType.Bishop),
    WhiteKing(Color.White, PieceType.King),

    BlackPawn(Color.Black, PieceType.Pawn),
    BlackRook(Color.Black, PieceType.Rook),
    BlackQueen(Color.Black, PieceType.Queen),
    BlackKnight(Color.Black, PieceType.Knight),
    BlackBishop(Color.Black, PieceType.Bishop),
    BlackKing(Color.Black, PieceType.King);

    private final Color color;
    private final PieceType type;

    Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public Color color(){
        return color;
    }

    public PieceType type(){
        return type;
    }


}
