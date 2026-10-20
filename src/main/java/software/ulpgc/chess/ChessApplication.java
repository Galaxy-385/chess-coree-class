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
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ChessApplication extends Application {

    private Game game;

    private final Button[][] squares = new Button[8][8];

    private Square selectedSquare;
    private Label turnLabel;
    private Label messageLabel;

    private final Set<Square> legalDestinations = new HashSet<>();
    private final Set<Square> captureDestinations = new HashSet<>();

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
                        + "2. Green squares show legal moves.\n\n"
                        + "3. Red squares show captures.\n\n"
                        + "4. Click a destination to move."
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
            selectPiece(clickedSquare, clickedPiece);
            return;
        }

        if (selectedSquare.equals(clickedSquare)) {
            clearSelection();
            messageLabel.setText("Selection cancelled.");
            refreshBoard();
            return;
        }

        // selecting another piece of the current player
        if (clickedPiece != null
                && clickedPiece.color() == game.turn()) {

            selectPiece(clickedSquare, clickedPiece);
            return;
        }

        // only attempt moves to calculated legal destinations
        if (!legalDestinations.contains(clickedSquare)) {
            messageLabel.setText("That move is not legal.");
            return;
        }

        try {
            Piece movingPiece = game.board().pieceAt(selectedSquare);
            Piece promotionPiece = null;

            boolean promotes =
                    movingPiece.type() == PieceType.Pawn
                            && (
                            (movingPiece.color() == Color.White
                                    && clickedSquare.rank() == Rank.R8)
                                    ||
                                    (movingPiece.color() == Color.Black
                                            && clickedSquare.rank() == Rank.R1)
                    );

            if (promotes) {
                Optional<Piece> choice =
                        choosePromotion(movingPiece.color());

                if (choice.isEmpty()) {
                    messageLabel.setText("Promotion cancelled.");
                    clearSelection();
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

            clearSelection();
            refreshBoard();
            updateGameStatus();

        } catch (IllegalArgumentException | IllegalStateException exception) {
            messageLabel.setText("That move is not legal.");
            clearSelection();
            refreshBoard();
        }
    }

    private void selectPiece(Square square, Piece piece) {
        if (piece == null || piece.color() != game.turn()) {
            messageLabel.setText(
                    "Select a piece of the current player."
            );
            return;
        }

        selectedSquare = square;
        calculateLegalDestinations(square);

        messageLabel.setText(
                "Selected " + square + ". Choose a destination."
        );

        refreshBoard();
    }

    private void calculateLegalDestinations(Square from) {
        legalDestinations.clear();
        captureDestinations.clear();

        Board board = game.board();
        Piece selectedPiece = board.pieceAt(from);

        if (selectedPiece == null) {
            return;
        }

        for (File file : File.values()) {
            for (Rank rank : Rank.values()) {
                Square to = new Square(file, rank);

                if (from.equals(to)) {
                    continue;
                }

                Piece promotionPiece = null;

                // use a queen to validate a pawn's last-rank move
                if (selectedPiece.type() == PieceType.Pawn
                        && (
                        (selectedPiece.color() == Color.White
                                && to.rank() == Rank.R8)
                                ||
                                (selectedPiece.color() == Color.Black
                                        && to.rank() == Rank.R1)
                )) {
                    promotionPiece = selectedPiece.color() == Color.White
                            ? Piece.WhiteQueen
                            : Piece.BlackQueen;
                }

                try {
                    // legalMove returns a new board; it does not change this one
                    board.legalMove(from, to, promotionPiece);

                    legalDestinations.add(to);

                    Piece target = board.pieceAt(to);

                    boolean isEnPassant =
                            selectedPiece.type() == PieceType.Pawn
                                    && to.equals(board.enPassantTarget())
                                    && from.file() != to.file()
                                    && target == null;

                    if ((target != null
                            && target.color() != selectedPiece.color())
                            || isEnPassant) {
                        captureDestinations.add(to);
                    }

                } catch (IllegalArgumentException exception) {
                    // this destination is not legal
                }
            }
        }
    }

    private void clearSelection() {
        selectedSquare = null;
        legalDestinations.clear();
        captureDestinations.clear();
    }

    private Optional<Piece> choosePromotion(Color color) {
        List<Piece> choices = color == Color.White
                ? List.of(
                Piece.WhiteQueen,
                Piece.WhiteRook,
                Piece.WhiteBishop,
                Piece.WhiteKnight
        )
                : List.of(
                Piece.BlackQueen,
                Piece.BlackRook,
                Piece.BlackBishop,
                Piece.BlackKnight
        );

        Dialog<Piece> dialog = new Dialog<>();

        dialog.setTitle("Pawn promotion");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("chess.css").toExternalForm()
        );

        dialog.getDialogPane().getStyleClass().add("promotion-dialog");

        Label title = new Label("PROMOTE YOUR PAWN");
        title.getStyleClass().add("promotion-title");

        Label subtitle = new Label("Choose the piece you want");
        subtitle.getStyleClass().add("promotion-subtitle");

        HBox pieceOptions = new HBox(12);
        pieceOptions.setAlignment(Pos.CENTER);

        final Piece[] selectedPiece = {choices.get(0)};

        for (Piece piece : choices) {
            Button option = new Button();
            option.getStyleClass().add("promotion-option");
            option.setGraphic(PieceSprite.create(piece));
            option.setPrefSize(88, 88);

            Label name = new Label(piece.type().toString());
            name.getStyleClass().add("promotion-piece-name");

            VBox optionBox = new VBox(6, option, name);
            optionBox.setAlignment(Pos.CENTER);

            option.setOnAction(event -> {
                selectedPiece[0] = piece;
                dialog.setResult(piece);
                dialog.close();
            });

            pieceOptions.getChildren().add(optionBox);
        }

        VBox content = new VBox(18, title, subtitle, pieceOptions);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> null);

        return dialog.showAndWait();
    }

    private void refreshBoard() {
        Board board = game.board();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Button button = squares[row][column];
                Square square = squareAt(row, column);
                Piece piece = board.pieceAt(square);

                button.setText("");
                button.setGraphic(
                        piece == null ? null : PieceSprite.create(piece)
                );

                button.getStyleClass().removeAll(
                        "white-piece",
                        "black-piece",
                        "light-square",
                        "dark-square",
                        "selected-square",
                        "legal-destination",
                        "capture-destination"
                );

                if (piece != null) {
                    button.getStyleClass().add(
                            piece.color() == Color.White
                                    ? "white-piece"
                                    : "black-piece"
                    );
                }

                boolean light = (row + column) % 2 == 0;

                button.getStyleClass().add(
                        light ? "light-square" : "dark-square"
                );

                if (square.equals(selectedSquare)) {
                    button.getStyleClass().add("selected-square");
                } else if (captureDestinations.contains(square)) {
                    button.getStyleClass().add("capture-destination");
                } else if (legalDestinations.contains(square)) {
                    button.getStyleClass().add("legal-destination");
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
        clearSelection();
        messageLabel.setText("New game started.");
        refreshBoard();
    }

    public static void main(String[] args) {
        launch(args);
    }
}