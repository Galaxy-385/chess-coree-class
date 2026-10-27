package software.ulpgc.chess.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.Room;
import software.ulpgc.chess.ports.RoomRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JoinRoomTest {

    private InMemoryRoomRepository repository;
    private CreateRoom createRoom;
    private JoinRoom joinRoom;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRoomRepository();
        createRoom = new CreateRoom(repository);
        joinRoom = new JoinRoom(repository);
    }

    @Test
    void shouldAllowSecondPlayerToJoinRoom() {
        createRoom.execute("room-1", "player-1");

        Room room = joinRoom.execute("room-1", "player-2");

        assertEquals("player-1", room.whitePlayerId());
        assertEquals("player-2", room.blackPlayerId());
        assertTrue(room.isFull());
    }

    @Test
    void shouldRejectEmptyRoomId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> joinRoom.execute("", "player-2")
        );
    }

    @Test
    void shouldRejectEmptyPlayerId() {
        createRoom.execute("room-1", "player-1");

        assertThrows(
                IllegalArgumentException.class,
                () -> joinRoom.execute("room-1", " ")
        );
    }

    @Test
    void shouldRejectNonexistentRoom() {
        assertThrows(
                IllegalArgumentException.class,
                () -> joinRoom.execute("room-404", "player-2")
        );
    }

    @Test
    void shouldRejectOriginalPlayerJoiningAgain() {
        createRoom.execute("room-1", "player-1");

        assertThrows(
                IllegalArgumentException.class,
                () -> joinRoom.execute("room-1", "player-1")
        );
    }

    @Test
    void shouldRejectThirdPlayerWhenRoomIsFull() {
        createRoom.execute("room-1", "player-1");
        joinRoom.execute("room-1", "player-2");

        assertThrows(
                IllegalStateException.class,
                () -> joinRoom.execute("room-1", "player-3")
        );

        Room room = repository.findById("room-1").orElseThrow();

        assertEquals("player-2", room.blackPlayerId());
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