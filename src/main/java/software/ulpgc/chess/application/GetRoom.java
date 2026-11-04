package software.ulpgc.chess.application;

import software.ulpgc.chess.domain.model.Room;
import software.ulpgc.chess.ports.RoomRepository;

import java.util.Objects;

public class GetRoom {

    private final RoomRepository roomRepository;

    public GetRoom(RoomRepository roomRepository) {
        this.roomRepository = Objects.requireNonNull(roomRepository);
    }

    public Room execute(String roomId) {

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException(
                    "Room ID cannot be empty"
            );
        }

        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));
    }
}