package software.ulpgc.chess.adapters.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.ulpgc.chess.domain.Game;
import software.ulpgc.chess.domain.Room;
import software.ulpgc.chess.domain.Square;
import software.ulpgc.chess.ports.GameRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostgresGameRepositoryTest {

    private PostgresGameRepository gameRepository;
    private PostgresRoomRepository roomRepository;

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
    void shouldSaveAndRestoreGame() {
        String roomId = UUID.randomUUID().toString();

        Game originalGame = new Game();
        originalGame.move(Square.at("e2"), Square.at("e4"));
        originalGame.move(Square.at("e7"), Square.at("e5"));

        Room room = new Room(roomId, "player-white", originalGame);

        roomRepository.save(room);

        Optional<Game> restored = gameRepository.findById(roomId);

        assertTrue(restored.isPresent());
        assertEquals(originalGame.turn(), restored.get().turn());
        assertEquals(originalGame.moves().size(), restored.get().moves().size());
        assertEquals(
                originalGame.board().pieceAt(Square.at("e4")),
                restored.get().board().pieceAt(Square.at("e4"))
        );
        assertEquals(
                originalGame.board().pieceAt(Square.at("e5")),
                restored.get().board().pieceAt(Square.at("e5"))
        );

        roomRepository.deleteById(roomId);
    }

    @Test
    void shouldReturnEmptyWhenGameDoesNotExist() {
        Optional<Game> result =
                gameRepository.findById(UUID.randomUUID().toString());

        assertTrue(result.isEmpty());
    }
}