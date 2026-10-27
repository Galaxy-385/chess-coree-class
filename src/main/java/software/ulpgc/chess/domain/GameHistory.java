
package software.ulpgc.chess.domain;

import java.util.HashMap;
import java.util.Map;

public class GameHistory {

    private final Map<PositionKey, Integer> positions;

    public GameHistory() {
        this.positions = new HashMap<>();
    }

    public void recordPosition(Board board, Color turn) {
        PositionKey position = new PositionKey(board, turn);

        positions.put(
                position,
                positions.getOrDefault(position, 0) + 1
        );
    }

    public int occurrences(Board board, Color turn) {
        PositionKey position = new PositionKey(board, turn);

        return positions.getOrDefault(position, 0);
    }

    public boolean isThreefoldRepetition(Board board, Color turn) {
        return occurrences(board, turn) >= 3;
    }
}
