
package software.ulpgc.chess;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;

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
    private Label eyebrow;
    private ListView<MoveRecord> moveHistory;

    private boolean chess960Mode = false;

    private Button classicButton;
    private Button chess960Button;

    private final Set<Square> legalDestinations = new HashSet<>();
    private final Set<Square> captureDestinations = new HashSet<>();

    private record MoveRecord(
            Piece piece,
            Square from,
            Square to,
            boolean capture,
            Piece promotionPiece,
            int moveNumber,
            Color color,
            String notation
    ) {}

    @Override
    public void start(Stage stage) {
        game = new Game();

        HBox root = new HBox(18);
        root.getStyleClass().add("app-root");
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER);

        // IZQUIERDA: título y panel de información
        Label gameTitle = new Label("CHESS TOGETHER");
        gameTitle.getStyleClass().add("game-title");

        VBox sidebar = createSidebar();

        VBox leftColumn = new VBox(10);
        leftColumn.getStyleClass().add("left-column");
        leftColumn.setPrefWidth(330);
        leftColumn.setMinWidth(310);
        leftColumn.setMaxWidth(330);
        leftColumn.getChildren().addAll(gameTitle, sidebar);
        VBox.setVgrow(sidebar, Priority.ALWAYS);

        // CENTRO: tablero
        GridPane boardView = createBoard();
        boardView.setPrefSize(580, 580);
        boardView.setMinSize(580, 580);
        boardView.setMaxSize(580, 580);

        StackPane boardContainer = new StackPane(boardView);
        boardContainer.setAlignment(Pos.CENTER);

        VBox boardColumn = new VBox();
        boardColumn.getStyleClass().add("board-column");
        boardColumn.setAlignment(Pos.CENTER);
        boardColumn.setMinWidth(580);
        boardColumn.getChildren().add(boardContainer);

        // DERECHA: extras y modos de juego
        VBox extrasPanel = new VBox(14);
        extrasPanel.getStyleClass().add("extras-panel");
        extrasPanel.setPadding(new Insets(18));

        Label extrasTitle = new Label("EXTRAS");
        extrasTitle.getStyleClass().add("section-label");

        classicButton = new Button("♙   Classic");
        classicButton.getStyleClass().add("mode-button");
        classicButton.setMaxWidth(Double.MAX_VALUE);

        chess960Button = new Button("♞   Chess960");
        chess960Button.getStyleClass().add("mode-button");
        chess960Button.setMaxWidth(Double.MAX_VALUE);

        classicButton.setOnAction(event -> {
            chess960Mode = false;
            updateModeButtons();
            newGame();
        });

        chess960Button.setOnAction(event -> {
            chess960Mode = true;
            updateModeButtons();
            newGame();
        });

        extrasPanel.getChildren().addAll(
                extrasTitle,
                classicButton,
                chess960Button
        );

        // Ilustración situada debajo de EXTRAS.
        // El PNG debe estar en el mismo paquete de recursos que chess.css.
        Node artwork = createArtwork();

        StackPane artworkPanel = new StackPane(artwork);
        artworkPanel.getStyleClass().add("artwork-panel");
        artworkPanel.setAlignment(Pos.CENTER);
        artworkPanel.setMinHeight(0);
        VBox.setVgrow(artworkPanel, Priority.ALWAYS);

        VBox rightColumn = new VBox(0);
        rightColumn.getStyleClass().add("right-column");
        rightColumn.setPrefWidth(370);
        rightColumn.setMinWidth(330);
        rightColumn.setMaxWidth(370);
        rightColumn.getChildren().addAll(extrasPanel, artworkPanel);
        VBox.setVgrow(artworkPanel, Priority.ALWAYS);

        // Distribución final de las tres columnas.
        root.getChildren().addAll(
                leftColumn,
                boardColumn,
                rightColumn
        );

        Scene scene = new Scene(root, 1500, 820);

        scene.getStylesheets().add(
                getClass().getResource("chess.css").toExternalForm()
        );

        stage.setTitle("Chess Together");
        stage.setMinWidth(1400);
        stage.setMinHeight(760);
        stage.setScene(scene);
        stage.show();

        refreshBoard();
        updateModeButtons();
    }

    private Node createArtwork() {
        var imageUrl = getClass().getResource(
                "/software/ulpgc/chess/chess-battle.png"
        );

        if (imageUrl == null) {
            Label placeholder = new Label(
                    "NO SE ENCUENTRA LA IMAGEN\n\n"
                            + "Comprueba que chess-battle.png esté en:\n"
                            + "src/main/resources/software/ulpgc/chess/"
            );

            placeholder.getStyleClass().add("artwork-placeholder");
            placeholder.setWrapText(true);
            placeholder.setMaxWidth(300);
            placeholder.setAlignment(Pos.CENTER);

            return placeholder;
        }

        Image image = new Image(imageUrl.toExternalForm());

        if (image.isError()) {
            Label error = new Label(
                    "No se pudo cargar la imagen:\n"
                            + image.getException()
            );

            error.getStyleClass().add("artwork-placeholder");
            error.setWrapText(true);
            return error;
        }

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setFitWidth(350);
        imageView.setFitHeight(490);

        return imageView;
    }

    private void updateModeButtons() {
        classicButton.getStyleClass().remove("mode-button-selected");
        chess960Button.getStyleClass().remove("mode-button-selected");

        if (chess960Mode) {
            chess960Button.getStyleClass().add("mode-button-selected");
        } else {
            classicButton.getStyleClass().add("mode-button-selected");
        }
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(330);
        sidebar.setMinWidth(310);
        sidebar.setMaxWidth(330);
        sidebar.setAlignment(Pos.TOP_LEFT);

        eyebrow = new Label("CHESS / CLASSIC");
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

        Label historyHeading = new Label("MOVE HISTORY");
        historyHeading.getStyleClass().add("section-label");

        moveHistory = new ListView<>();
        moveHistory.getStyleClass().add("move-history");
        moveHistory.setPlaceholder(
                new Label("Moves will appear here")
        );
        moveHistory.setMaxWidth(Double.MAX_VALUE);
        moveHistory.setMinHeight(130);
        VBox.setVgrow(moveHistory, Priority.ALWAYS);

        moveHistory.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MoveRecord move, boolean empty) {
                super.updateItem(move, empty);

                if (empty || move == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                StackPane pieceIcon = PieceSprite.create(move.piece());
                pieceIcon.setMinSize(28, 28);
                pieceIcon.setPrefSize(28, 28);
                pieceIcon.setMaxSize(28, 28);

                Label movement = new Label(
                        move.from().toString().replace("Square", "")
                                + (move.capture() ? " x " : " → ")
                                + move.to().toString().replace("Square", "")
                );
                movement.getStyleClass().add("history-move");
                movement.setWrapText(false);

                Label details = new Label(
                        move.moveNumber()
                                + (move.color() == Color.White ? "." : "...")
                );
                details.getStyleClass().add("history-details");

                VBox text = new VBox(3, movement, details);
                text.setAlignment(Pos.CENTER_LEFT);
                text.setMinWidth(0);
                HBox.setHgrow(text, Priority.ALWAYS);

                HBox row = new HBox(5, pieceIcon, text);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(3, 4, 3, 4));
                row.setMaxWidth(Double.MAX_VALUE);

                setGraphic(row);
                setText(null);
            }
        });

        sidebar.getChildren().addAll(
                eyebrow,
                title,
                subtitle,
                separator,
                turnHeading,
                turnLabel,
                messageLabel,
                newGameButton,
                historyHeading,
                moveHistory
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

        if (clickedPiece != null && clickedPiece.color() == game.turn()) {
            selectPiece(clickedSquare, clickedPiece);
            return;
        }

        if (!legalDestinations.contains(clickedSquare)) {
            messageLabel.setText("That move is not legal.");
            return;
        }

        try {
            Square from = selectedSquare;
            Color movingColor = game.turn();
            Piece movingPiece = game.board().pieceAt(from);

            boolean isCapture = captureDestinations.contains(clickedSquare);
            Piece promotionPiece = null;

            boolean promotes =
                    movingPiece.type() == PieceType.Pawn
                            && ((movingColor == Color.White
                            && clickedSquare.rank() == Rank.R8)
                            || (movingColor == Color.Black
                            && clickedSquare.rank() == Rank.R1));

            if (promotes) {
                Optional<Piece> choice = choosePromotion(movingColor);

                if (choice.isEmpty()) {
                    messageLabel.setText("Promotion cancelled.");
                    clearSelection();
                    refreshBoard();
                    return;
                }

                promotionPiece = choice.get();
            }

            String notation = AlgebraicNotation.beforeMove(
                    game.board(),
                    from,
                    clickedSquare,
                    promotionPiece
            );

            if (promotionPiece == null) {
                game.move(from, clickedSquare);
            } else {
                game.move(from, clickedSquare, promotionPiece);
            }

            notation = AlgebraicNotation.withCheckSuffix(notation, game);

            int moveNumber = moveHistory.getItems().size() / 2 + 1;

            moveHistory.getItems().add(
                    new MoveRecord(
                            movingPiece,
                            from,
                            clickedSquare,
                            isCapture,
                            promotionPiece,
                            moveNumber,
                            movingColor,
                            notation
                    )
            );

            moveHistory.scrollTo(moveHistory.getItems().size() - 1);

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
            messageLabel.setText("Select a piece of the current player.");
            return;
        }

        selectedSquare = square;
        calculateLegalDestinations(square);

        messageLabel.setText("Selected " + square);

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

                if (selectedPiece.type() == PieceType.Pawn
                        && ((selectedPiece.color() == Color.White
                        && to.rank() == Rank.R8)
                        || (selectedPiece.color() == Color.Black
                        && to.rank() == Rank.R1))) {
                    promotionPiece = selectedPiece.color() == Color.White
                            ? Piece.WhiteQueen
                            : Piece.BlackQueen;
                }

                try {
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
                    // Este destino no es legal.
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
                                + (piece == null ? ", empty" : ", " + piece)
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
            messageLabel.setText("The game has ended in a draw.");
        } else {
            messageLabel.setText(
                    game.turn() == Color.White
                            ? "White's turn."
                            : "Black's turn."
            );
        }
    }

    private void newGame() {
        game = chess960Mode
                ? new Game(Board.chess960())
                : new Game();

        clearSelection();
        moveHistory.getItems().clear();

        eyebrow.setText(
                chess960Mode ? "CHESS / CHESS960" : "CHESS / CLASSIC"
        );

        messageLabel.setText(
                chess960Mode
                        ? "New Chess960 game started."
                        : "New classic game started."
        );

        refreshBoard();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
