
package software.ulpgc.chess.domain;

import java.util.Objects;

public class Room {

    private final String id;
    private String whitePlayerId;
    private String blackPlayerId;
    private final Game game;

    public Room(String id, String whitePlayerId, Game game) {
        this.id = Objects.requireNonNull(id);
        this.whitePlayerId = Objects.requireNonNull(whitePlayerId);
        this.game = Objects.requireNonNull(game);
        this.blackPlayerId = null;
    }

    public String id() {
        return id;
    }

    public String whitePlayerId() {
        return whitePlayerId;
    }

    public String blackPlayerId() {
        return blackPlayerId;
    }

    public Game game() {
        return game;
    }

    public boolean isFull() {
        return blackPlayerId != null;
    }

    public void join(String playerId) {
        Objects.requireNonNull(playerId);

        if (isFull()) {
            throw new IllegalStateException("The room is full");
        }

        if (whitePlayerId.equals(playerId)) {
            throw new IllegalArgumentException(
                    "The player is already in the room"
            );
        }

        blackPlayerId = playerId;
    }
}
