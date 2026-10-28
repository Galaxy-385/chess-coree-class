package software.ulpgc.chess.domain;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Board board;
    private Color turn;
    private final GameHistory history;
    private final HalfMoveClock halfMoveClock;
    private int fullMoveNumber;
    private final List<PlayedMove> moves;
    private final Board initialBoard;

    public Game() {
        this.board = Board.initial();
        this.initialBoard = this.board;
        this.turn = Color.White;
        this.history = new GameHistory();
        this.halfMoveClock = new HalfMoveClock();
        this.fullMoveNumber = 1;
        this.moves = new ArrayList<>();
        history.recordPosition(board, turn);
    }

    public Game(Board board) {
        this.board = board;
        this.initialBoard = board;
        this.turn = Color.White;
        this.history = new GameHistory();
        this.halfMoveClock = new HalfMoveClock();
        this.fullMoveNumber = 1;
        this.moves = new ArrayList<>();
        history.recordPosition(board, turn);
    }

    public Game(Board board, Color turn) {
        this.board = board;
        this.initialBoard = board;
        this.turn = turn;
        this.history = new GameHistory();
        this.halfMoveClock = new HalfMoveClock();
        this.fullMoveNumber = 1;
        this.moves = new ArrayList<>();
        history.recordPosition(board, turn);
    }

    public Board board() {
        return board;
    }

    public Color turn() {
        return turn;
    }

    public int halfMoveClock() {
        return halfMoveClock.count();
    }

    public int fullMoveNumber() {
        return fullMoveNumber;
    }
    public Board initialBoard() {
        return initialBoard;
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

        moves.add(new PlayedMove(
                fullMoveNumber,
                from,
                to,
                null
        ));

        halfMoveClock.recordMove(piece, capturedPiece);

        if (turn == Color.Black) {
            fullMoveNumber++;
        }

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

        moves.add(new PlayedMove(
                fullMoveNumber,
                from,
                to,
                promotionPiece
        ));

        halfMoveClock.recordMove(piece, capturedPiece);

        if (turn == Color.Black) {
            fullMoveNumber++;
        }

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

    public List<PlayedMove> moves() {
        return List.copyOf(moves);
    }

    public static Game restore(Board initialBoard, Color initialTurn,
                               List<PlayedMove> moves) {
        Game game = new Game(initialBoard, initialTurn);

        for (PlayedMove move : moves) {
            if (move.promotionPiece() == null) {
                game.move(move.from(), move.to());
            } else {
                game.move(move.from(), move.to(), move.promotionPiece());
            }
        }

        return game;
    }
}