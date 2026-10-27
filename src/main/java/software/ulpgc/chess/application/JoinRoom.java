package software.ulpgc.chess.application;

import software.ulpgc.chess.domain.Room;
import software.ulpgc.chess.ports.RoomRepository;

import java.util.Objects;

public class JoinRoom {

    private final RoomRepository roomRepository;

    public JoinRoom(RoomRepository roomRepository) {
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

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Room does not exist"
                ));

        room.join(playerId);
        roomRepository.save(room);

        return room;
    }
}