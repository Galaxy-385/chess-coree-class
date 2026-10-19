package software.ulpgc.chess;

public class Game {

    private Board board;
    private Color turn;
    private final GameHistory history;

    public Game(){
        this.board = Board.initial();
        this.turn = Color.White;
        this.history = new GameHistory();
        history.recordPosition(board, turn);
    }

    public Game(Board board){
        this.board = board;
        this.turn = Color.White;
        this.history = new GameHistory();
        history.recordPosition(board, turn);
    }

    public Game(Board board, Color turn){
        this.board = board;
        this.turn = turn;
        this.history = new GameHistory();
        history.recordPosition(board, turn);
    }

    public Board board(){
        return board;
    }

    public Color turn(){
        return turn;
    }

    public void move(Square from, Square to){
        Piece piece = board.pieceAt(from);

        if(piece == null || piece.color() != turn){
            throw new IllegalArgumentException(
                    "No piece of the current player on this square"
            );
        }

        board = board.legalMove(from, to);

        turn = turn == Color.White ? Color.Black : Color.White;

        history.recordPosition(board, turn);
    }

    public boolean isThreefoldRepetition(){
        return history.isThreefoldRepetition(board, turn);
    }


    public boolean isCheckmate(){
        CheckmateDetector checkmateDetector = new CheckmateDetector();
        return checkmateDetector.isCheckmate(board, turn);
    }

    public boolean isStalemate(){
        StalemateDetector stalemateDetector = new StalemateDetector();
        return stalemateDetector.isStalemate(board, turn);
    }

    public boolean isDraw(){
        DrawDetector drawDetector = new DrawDetector();
        return drawDetector.isDraw(board, turn) || isThreefoldRepetition();
    }

    public void move(Square from, Square to, Piece promotionPiece){
        Piece piece = board.pieceAt(from);

        if(piece == null || piece.color() != turn){
            throw new IllegalArgumentException(
                    "No piece of the current player on this square"
            );
        }

        board = board.legalMove(from, to, promotionPiece);

        turn = turn == Color.White ? Color.Black : Color.White;

        history.recordPosition(board, turn);
    }

}
