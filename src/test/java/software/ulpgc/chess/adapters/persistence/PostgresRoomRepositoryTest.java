package software.ulpgc.chess.adapters.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.Game;
import software.ulpgc.chess.domain.Room;
import software.ulpgc.chess.domain.Square;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostgresRoomRepositoryTest {

    private PostgresRoomRepository roomRepository;
    private PostgresGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        String password = System.getenv()
                .getOrDefault("POSTGRES_PASSWORD", "chess_local_dev");

        PostgresConnection connection = new PostgresConnection(
                "jdbc:postgresql://localhost:5432/chess",
                "chess_user",
                password
        );

        gameRepository = new PostgresGameRepository(connection);
        roomRepository = new PostgresRoomRepository(
                connection,
                gameRepository
        );
    }

    @Test
    void shouldSaveAndFindRoom() {
        String roomId = UUID.randomUUID().toString();

        Room originalRoom = new Room(
                roomId,
                "player-white",
                new Game()
        );

        roomRepository.save(originalRoom);

        Optional<Room> result = roomRepository.findById(roomId);

        assertTrue(result.isPresent());
        assertEquals(roomId, result.get().id());
        assertEquals("player-white", result.get().whitePlayerId());
        assertNull(result.get().blackPlayerId());
        assertNotNull(result.get().game());

        roomRepository.deleteById(roomId);
    }

    @Test
    void shouldAllowSecondPlayerToJoinRoom() {
        String roomId = UUID.randomUUID().toString();

        Room room = new Room(
                roomId,
                "player-white",
                new Game()
        );

        roomRepository.save(room);

        room.join("player-black");
        roomRepository.save(room);

        Optional<Room> result = roomRepository.findById(roomId);

        assertTrue(result.isPresent());
        assertEquals("player-white", result.get().whitePlayerId());
        assertEquals("player-black", result.get().blackPlayerId());
        assertTrue(result.get().isFull());

        roomRepository.deleteById(roomId);
    }

    @Test
    void shouldReturnEmptyWhenRoomDoesNotExist() {
        Optional<Room> result =
                roomRepository.findById(UUID.randomUUID().toString());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteRoomAndAssociatedGame() {
        String roomId = UUID.randomUUID().toString();

        Room room = new Room(
                roomId,
                "player-white",
                new Game()
        );

        roomRepository.save(room);

        assertTrue(roomRepository.findById(roomId).isPresent());
        assertTrue(gameRepository.findById(roomId).isPresent());

        roomRepository.deleteById(roomId);

        assertTrue(roomRepository.findById(roomId).isEmpty());
        assertTrue(gameRepository.findById(roomId).isEmpty());
    }

    @Test
    void shouldPreserveGameWhenRoomIsRetrieved() {
        String roomId = UUID.randomUUID().toString();

        Game game = new Game();
        game.move(Square.at("e2"), Square.at("e4"));
        game.move(Square.at("e7"), Square.at("e5"));

        Room room = new Room(roomId, "player-white", game);

        roomRepository.save(room);

        Optional<Room> result = roomRepository.findById(roomId);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().game().moves().size());
        assertEquals(game.turn(), result.get().game().turn());

        roomRepository.deleteById(roomId);
    }
}