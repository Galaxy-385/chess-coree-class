package software.ulpgc.chess;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class ChessApplication extends Application {

    private Game game;
    private final Button[][] squares = new Button[8][8];

    private Square selectedSquare;
    private Label turnLabel;
    private Label messageLabel;

    @Override
    public void start(Stage stage) {
        game = new Game();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");

        VBox sidebar = createSidebar();
        GridPane boardView = createBoard();

        root.setCenter(boardView);
        root.setRight(sidebar);

        BorderPane.setMargin(boardView, new Insets(30));
        BorderPane.setMargin(sidebar, new Insets(30, 30, 30, 0));

        Scene scene = new Scene(root, 1120, 760);

        scene.getStylesheets().add(
                getClass().getResource("chess.css").toExternalForm()
        );

        stage.setTitle("Chess");
        stage.setMinWidth(850);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();

        refreshBoard();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(18);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(270);
        sidebar.setAlignment(Pos.TOP_LEFT);

        Label eyebrow = new Label("CHESS / CLASSIC");
        eyebrow.getStyleClass().add("eyebrow");

        Label title = new Label("Let's play.");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("A quiet game of strategy.");
        subtitle.getStyleClass().add("subtitle");

        Region separator = new Region();
        separator.getStyleClass().add("separator");
        separator.setMinHeight(1);
        separator.setMaxHeight(1);

        Label turnHeading = new Label("CURRENT TURN");
        turnHeading.getStyleClass().add("section-label");

        turnLabel = new Label();
        turnLabel.getStyleClass().add("turn-label");

        messageLabel = new Label("Select a piece to begin.");
        messageLabel.getStyleClass().add("message-label");
        messageLabel.setWrapText(true);

        Button newGameButton = new Button("New game");
        newGameButton.getStyleClass().add("primary-button");
        newGameButton.setMaxWidth(Double.MAX_VALUE);
        newGameButton.setOnAction(event -> newGame());

        Label help = new Label(
                "HOW TO PLAY\n\n"
                        + "1. Click one of your pieces.\n\n"
                        + "2. Click the destination square.\n\n"
                        + "3. The game checks whether the move is legal."
        );
        help.getStyleClass().add("help-text");
        help.setWrapText(true);

        sidebar.getChildren().addAll(
                eyebrow,
                title,
                subtitle,
                separator,
                turnHeading,
                turnLabel,
                messageLabel,
                newGameButton,
                help
        );

        return sidebar;
    }

    private GridPane createBoard() {
        GridPane boardView = new GridPane();
        boardView.getStyleClass().add("chess-board");
        boardView.setAlignment(Pos.CENTER);

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Button squareButton = new Button();

                squareButton.getStyleClass().add("square");
                squareButton.setMaxSize(
                        Double.MAX_VALUE,
                        Double.MAX_VALUE
                );
                squareButton.setMinSize(0, 0);

                final int boardRow = row;
                final int boardColumn = column;

                squareButton.setOnAction(event ->
                        handleSquareClick(boardRow, boardColumn)
                );

                squares[row][column] = squareButton;
                boardView.add(squareButton, column, row);

                GridPane.setHgrow(squareButton, Priority.ALWAYS);
                GridPane.setVgrow(squareButton, Priority.ALWAYS);
            }
        }

        for (int i = 0; i < 8; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(12.5);
            column.setHgrow(Priority.ALWAYS);
            boardView.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(12.5);
            row.setVgrow(Priority.ALWAYS);
            boardView.getRowConstraints().add(row);
        }

        return boardView;
    }

    private void handleSquareClick(int row, int column) {
        Square clickedSquare = squareAt(row, column);
        Piece clickedPiece = game.board().pieceAt(clickedSquare);

        if (selectedSquare == null) {
            if (clickedPiece != null
                    && clickedPiece.color() == game.turn()) {

                selectedSquare = clickedSquare;
                messageLabel.setText(
                        "Selected " + clickedSquare
                                + ". Choose a destination."
                );
                refreshBoard();
            } else {
                messageLabel.setText(
                        "Select a piece of the current player."
                );
            }

            return;
        }

        if (selectedSquare.equals(clickedSquare)) {
            selectedSquare = null;
            messageLabel.setText("Selection cancelled.");
            refreshBoard();
            return;
        }

        if (clickedPiece != null
                && clickedPiece.color() == game.turn()) {

            selectedSquare = clickedSquare;
            messageLabel.setText("Piece selected.");
            refreshBoard();
            return;
        }

        try {
            Piece movingPiece =
                    game.board().pieceAt(selectedSquare);

            Piece promotionPiece = null;

            boolean promotes =
                    movingPiece.type() == PieceType.Pawn
                            && ((movingPiece.color() == Color.White
                            && clickedSquare.rank() == Rank.R8)
                            || (movingPiece.color() == Color.Black
                            && clickedSquare.rank() == Rank.R1));

            if (promotes) {
                Optional<Piece> choice =
                        choosePromotion(movingPiece.color());

                if (choice.isEmpty()) {
                    messageLabel.setText("Promotion cancelled.");
                    selectedSquare = null;
                    refreshBoard();
                    return;
                }

                promotionPiece = choice.get();
            }

            if (promotionPiece == null) {
                game.move(selectedSquare, clickedSquare);
            } else {
                game.move(
                        selectedSquare,
                        clickedSquare,
                        promotionPiece
                );
            }

            selectedSquare = null;
            refreshBoard();
            updateGameStatus();

        } catch (IllegalArgumentException | IllegalStateException exception) {
            messageLabel.setText("That move is not legal.");
            selectedSquare = null;
            refreshBoard();
        }
    }

    private Optional<Piece> choosePromotion(Color color) {
        List<Piece> choices;

        if (color == Color.White) {
            choices = List.of(
                    Piece.WhiteQueen,
                    Piece.WhiteRook,
                    Piece.WhiteBishop,
                    Piece.WhiteKnight
            );
        } else {
            choices = List.of(
                    Piece.BlackQueen,
                    Piece.BlackRook,
                    Piece.BlackBishop,
                    Piece.BlackKnight
            );
        }

        ChoiceDialog<Piece> dialog =
                new ChoiceDialog<>(choices.get(0), choices);

        dialog.setTitle("Pawn promotion");
        dialog.setHeaderText("Choose your new piece");
        dialog.setContentText("Promote to:");

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("chess.css").toExternalForm()
        );

        return dialog.showAndWait();
    }

    private void refreshBoard() {
        Board board = game.board();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Button button = squares[row][column];
                Square square = squareAt(row, column);
                Piece piece = board.pieceAt(square);

                button.setText(
                        piece == null ? "" : symbolFor(piece)
                );

                button.getStyleClass().removeAll(
                        "white-piece",
                        "black-piece"
                );

                if (piece != null) {
                    button.getStyleClass().add(
                            piece.color() == Color.White
                                    ? "white-piece"
                                    : "black-piece"
                    );
                }

                button.getStyleClass().removeAll(
                        "light-square",
                        "dark-square",
                        "selected-square"
                );

                boolean light = (row + column) % 2 == 0;

                button.getStyleClass().add(
                        light ? "light-square" : "dark-square"
                );

                if (square.equals(selectedSquare)) {
                    button.getStyleClass().add("selected-square");
                }

                button.setAccessibleText(
                        square.toString()
                                + (piece == null
                                ? ", empty"
                                : ", " + piece)
                );
            }
        }

        turnLabel.setText(
                game.turn() == Color.White
                        ? "White to move"
                        : "Black to move"
        );
    }

    private Square squareAt(int row, int column) {
        File file = File.values()[column];
        Rank rank = Rank.values()[7 - row];

        return new Square(file, rank);
    }

    private String symbolFor(Piece piece) {
        return switch (piece) {
            case WhiteKing -> "♔";
            case WhiteQueen -> "♕";
            case WhiteRook -> "♖";
            case WhiteBishop -> "♗";
            case WhiteKnight -> "♘";
            case WhitePawn -> "♙";
            case BlackKing -> "♚";
            case BlackQueen -> "♛";
            case BlackRook -> "♜";
            case BlackBishop -> "♝";
            case BlackKnight -> "♞";
            case BlackPawn -> "♟";
        };
    }

    private void updateGameStatus() {
        if (game.isCheckmate()) {
            turnLabel.setText("Checkmate");
            messageLabel.setText(
                    (game.turn() == Color.White ? "Black" : "White")
                            + " wins!"
            );
        } else if (game.isStalemate() || game.isDraw()) {
            turnLabel.setText("Draw");
            messageLabel.setText(
                    "The game has ended in a draw."
            );
        } else {
            messageLabel.setText(
                    game.turn() == Color.White
                            ? "White's turn."
                            : "Black's turn."
            );
        }
    }

    private void newGame() {
        game = new Game();
        selectedSquare = null;
        messageLabel.setText("New game started.");
        refreshBoard();
    }

    public static void main(String[] args) {
        launch(args);
    }
}