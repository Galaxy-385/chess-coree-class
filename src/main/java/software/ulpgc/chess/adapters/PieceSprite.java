package software.ulpgc.chess.adapters;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import software.ulpgc.chess.domain.Piece;

public class PieceSprite {

    private PieceSprite() {
    }

    public static StackPane create(Piece piece) {
        Text symbol = new Text(symbolFor(piece));

        boolean white = piece.color() == software.ulpgc.chess.domain.Color.White;

        symbol.setFont(Font.font(
                "DejaVu Sans",
                FontWeight.BOLD,
                52
        ));

        symbol.setFill(
                white
                        ? Color.web("#fff4d6")
                        : Color.web("#171717")
        );

        symbol.setStroke(
                white
                        ? Color.web("#171717")
                        : Color.web("#fff4d6")
        );

        symbol.setStrokeWidth(1.5);

        StackPane pane = new StackPane(symbol);
        pane.setMouseTransparent(true);

        return pane;
    }

    private static String symbolFor(Piece piece) {
        return switch (piece.type()) {
            case King -> "♚";
            case Queen -> "♛";
            case Rook -> "♜";
            case Bishop -> "♝";
            case Knight -> "♞";
            case Pawn -> "♟";
        };
    }
}