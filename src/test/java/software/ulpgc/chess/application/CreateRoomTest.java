
package software.ulpgc.chess.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.Room;
import software.ulpgc.chess.ports.RoomRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CreateRoomTest {

    private InMemoryRoomRepository repository;
    private CreateRoom createRoom;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRoomRepository();
        createRoom = new CreateRoom(repository);
    }

    @Test
    void shouldCreateAndSaveRoom() {
        Room room = createRoom.execute("room-1", "player-1");

        assertEquals("room-1", room.id());
        assertEquals("player-1", room.whitePlayerId());
        assertFalse(room.isFull());

        assertTrue(repository.findById("room-1").isPresent());
        assertSame(room, repository.findById("room-1").orElseThrow());
    }

    @Test
    void shouldRejectEmptyRoomId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createRoom.execute("", "player-1")
        );

        assertTrue(repository.rooms.isEmpty());
    }

    @Test
    void shouldRejectEmptyPlayerId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createRoom.execute("room-1", " ")
        );

        assertTrue(repository.rooms.isEmpty());
    }

    @Test
    void shouldRejectDuplicateRoomId() {
        createRoom.execute("room-1", "player-1");

        assertThrows(
                IllegalArgumentException.class,
                () -> createRoom.execute("room-1", "player-2")
        );

        assertEquals(1, repository.rooms.size());
    }

    private static class InMemoryRoomRepository
            implements RoomRepository {

        private final Map<String, Room> rooms = new HashMap<>();

        @Override
        public void save(Room room) {
            rooms.put(room.id(), room);
        }

        @Override
        public Optional<Room> findById(String roomId) {
            return Optional.ofNullable(rooms.get(roomId));
        }

        @Override
        public void deleteById(String roomId) {
            rooms.remove(roomId);
        }
    }
}
