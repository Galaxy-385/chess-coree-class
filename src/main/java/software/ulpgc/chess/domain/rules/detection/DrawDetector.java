package software.ulpgc.chess.domain.rules.detection;

import software.ulpgc.chess.domain.model.Board;
import software.ulpgc.chess.domain.model.Color;

public class DrawDetector {

    public boolean isDraw(Board board, Color color) {
        StalemateDetector stalemateDetector = new StalemateDetector();
        MaterialDrawDetector materialDrawDetector =
                new MaterialDrawDetector();

        return stalemateDetector.isStalemate(board, color)
                || materialDrawDetector.isInsufficientMaterial(board);
    }
}