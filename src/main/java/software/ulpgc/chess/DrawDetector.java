package software.ulpgc.chess;

public class DrawDetector {

    public boolean isDraw(Board board, Color color) {
        StalemateDetector stalemateDetector = new StalemateDetector();
        MaterialDrawDetector materialDrawDetector =
                new MaterialDrawDetector();

        return stalemateDetector.isStalemate(board, color)
                || materialDrawDetector.isInsufficientMaterial(board);
    }
}