package software.ulpgc.chess.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.model.Room;
import software.ulpgc.chess.domain.model.Square;
import software.ulpgc.chess.ports.RoomRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlayMoveTest {

    private InMemoryRoomRepository repository;
    private CreateRoom createRoom;
    private JoinRoom joinRoom;
    private PlayMove playMove;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRoomRepository();

        createRoom = new CreateRoom(repository);
        joinRoom = new JoinRoom(repository);
        playMove = new PlayMove(repository);

        createRoom.execute("room-1", "player-1");
        joinRoom.execute("room-1", "player-2");
    }

    @Test
    void shouldAllowWhitePlayerToMove() {
        Room room = playMove.execute(
                "room-1",
                "player-1",
                "e2",
                "e4",
                null
        );

        assertEquals(Square.at("e4"), room.game().board().pieceAt(Square.at("e4")) != null
                ? Square.at("e4")
                : null);

        assertEquals(1, room.game().moves().size());
        assertEquals("player-1", room.whitePlayerId());
    }

    @Test
    void shouldAllowBlackPlayerToMoveAfterWhite() {
        playMove.execute("room-1", "player-1", "e2", "e4", null);

        Room room = playMove.execute(
                "room-1",
                "player-2",
                "e7",
                "e5",
                null
        );

        assertEquals(2, room.game().moves().size());
    }

    @Test
    void shouldRejectPlayerWhoDoesNotBelongToRoom() {
        assertThrows(
                IllegalArgumentException.class,
                () -> playMove.execute(
                        "room-1",
                        "player-3",
                        "e2",
                        "e4",
                        null
                )
        );

        assertTrue(repository.findById("room-1")
                .orElseThrow().game().moves().isEmpty());
    }

    @Test
    void shouldRejectMoveWhenItIsNotPlayersTurn() {
        assertThrows(
                IllegalStateException.class,
                () -> playMove.execute(
                        "room-1",
                        "player-2",
                        "e7",
                        "e5",
                        null
                )
        );

        assertTrue(repository.findById("room-1")
                .orElseThrow().game().moves().isEmpty());
    }

    @Test
    void shouldRejectIllegalMove() {
        assertThrows(
                IllegalArgumentException.class,
                () -> playMove.execute(
                        "room-1",
                        "player-1",
                        "e2",
                        "e5",
                        null
                )
        );

        assertTrue(repository.findById("room-1")
                .orElseThrow().game().moves().isEmpty());
    }

    @Test
    void shouldRejectMoveInNonexistentRoom() {
        assertThrows(
                RoomNotFoundException.class,
                () -> playMove.execute(
                        "unknown-room",
                        "player-1",
                        "e2",
                        "e4",
                        null
                )
        );
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