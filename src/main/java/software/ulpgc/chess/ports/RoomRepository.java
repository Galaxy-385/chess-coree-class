package software.ulpgc.chess.ports;

import software.ulpgc.chess.domain.model.Room;

import java.util.Optional;

public interface RoomRepository {

    void save(Room room);

    Optional<Room> findById(String roomId);

    void deleteById(String roomId);
}
