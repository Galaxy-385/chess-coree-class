package software.ulpgc.chess;

public class Game {

    private Board board;
    private Color turn;
    private final GameHistory history;
    private final HalfMoveClock halfMoveClock;

    public Game() {
        this.board = Board.initial();
        this.turn = Color.White;
        this.history = new GameHistory();
        this.halfMoveClock = new HalfMoveClock();

        history.recordPosition(board, turn);
    }

    public Game(Board board) {
        this.board = board;
        this.turn = Color.White;
        this.history = new GameHistory();
        this.halfMoveClock = new HalfMoveClock();

        history.recordPosition(board, turn);
    }

    public Game(Board board, Color turn) {
        this.board = board;
        this.turn = turn;
        this.history = new GameHistory();
        this.halfMoveClock = new HalfMoveClock();

        history.recordPosition(board, turn);
    }

    public Board board() {
        return board;
    }

    public Color turn() {
        return turn;
    }

    public void move(Square from, Square to) {
        checkGameIsNotOver();

        Piece piece = board.pieceAt(from);

        if (piece == null || piece.color() != turn) {
            throw new IllegalArgumentException(
                    "No piece of the current player on this square"
            );
        }

        Piece capturedPiece = board.pieceAt(to);

        board = board.legalMove(from, to);

        halfMoveClock.recordMove(piece, capturedPiece);

        turn = turn == Color.White ? Color.Black : Color.White;

        history.recordPosition(board, turn);
    }

    public void move(Square from, Square to, Piece promotionPiece) {
        checkGameIsNotOver();

        Piece piece = board.pieceAt(from);

        if (piece == null || piece.color() != turn) {
            throw new IllegalArgumentException(
                    "No piece of the current player on this square"
            );
        }

        Piece capturedPiece = board.pieceAt(to);

        board = board.legalMove(from, to, promotionPiece);

        halfMoveClock.recordMove(piece, capturedPiece);

        turn = turn == Color.White ? Color.Black : Color.White;

        history.recordPosition(board, turn);
    }

    public boolean isThreefoldRepetition() {
        return history.isThreefoldRepetition(board, turn);
    }

    public boolean isCheckmate() {
        CheckmateDetector checkmateDetector = new CheckmateDetector();
        return checkmateDetector.isCheckmate(board, turn);
    }

    public boolean isStalemate() {
        StalemateDetector stalemateDetector = new StalemateDetector();
        return stalemateDetector.isStalemate(board, turn);
    }

    public boolean isDraw() {
        DrawDetector drawDetector = new DrawDetector();

        return drawDetector.isDraw(board, turn)
                || isThreefoldRepetition()
                || halfMoveClock.isDraw();
    }

    private void checkGameIsNotOver() {
        if (isGameOver()) {
            throw new IllegalStateException("The game has already ended");
        }
    }

    public boolean isGameOver() {
        return isCheckmate() || isDraw();
    }

}