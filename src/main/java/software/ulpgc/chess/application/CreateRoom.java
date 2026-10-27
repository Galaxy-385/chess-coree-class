
package software.ulpgc.chess.application;

import software.ulpgc.chess.domain.Game;
import software.ulpgc.chess.domain.Room;
import software.ulpgc.chess.ports.RoomRepository;

import java.util.Objects;

public class CreateRoom {

    private final RoomRepository roomRepository;

    public CreateRoom(RoomRepository roomRepository) {
        this.roomRepository = Objects.requireNonNull(roomRepository);
    }

    public Room execute(String roomId, String playerId) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException(
                    "Room ID cannot be empty"
            );
        }

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Player ID cannot be empty"
            );
        }

        if (roomRepository.findById(roomId).isPresent()) {
            throw new IllegalArgumentException(
                    "A room with this ID already exists"
            );
        }

        Game game = new Game();
        Room room = new Room(roomId, playerId, game);

        roomRepository.save(room);

        return room;
    }
}
