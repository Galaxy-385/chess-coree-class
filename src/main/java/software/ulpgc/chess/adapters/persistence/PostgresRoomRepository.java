
package software.ulpgc.chess.adapters.persistence;

import software.ulpgc.chess.domain.Game;
import software.ulpgc.chess.domain.Room;
import software.ulpgc.chess.ports.RoomRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PostgresRoomRepository implements RoomRepository {

    private final PostgresConnection connection;

    public PostgresRoomRepository(PostgresConnection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Room room) {
        String sql = """
                INSERT INTO rooms (id, white_player_id, black_player_id)
                VALUES (?, ?, ?)
                ON CONFLICT (id) DO UPDATE SET
                    white_player_id = EXCLUDED.white_player_id,
                    black_player_id = EXCLUDED.black_player_id
                """;

        try (Connection conn = connection.open();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, room.id());
            statement.setString(2, room.whitePlayerId());
            statement.setString(3, room.blackPlayerId());
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not save room " + room.id(), exception);
        }
    }

    @Override
    public Optional<Room> findById(String roomId) {
        String sql = """
                SELECT id, white_player_id, black_player_id
                FROM rooms
                WHERE id = ?
                """;

        try (Connection conn = connection.open();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, roomId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                String id = result.getString("id");
                String whitePlayerId = result.getString("white_player_id");
                String blackPlayerId = result.getString("black_player_id");

                Room room = new Room(id, whitePlayerId, new Game());

                if (blackPlayerId != null) {
                    room.join(blackPlayerId);
                }

                return Optional.of(room);
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not find room " + roomId, exception);
        }
    }

    @Override
    public void deleteById(String roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";

        try (Connection conn = connection.open();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, roomId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Could not delete room " + roomId, exception);
        }
    }
}
