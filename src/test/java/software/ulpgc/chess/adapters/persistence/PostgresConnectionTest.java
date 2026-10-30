
package software.ulpgc.chess.adapters.persistence;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostgresConnectionTest {

    @Test
    void shouldConnectToPostgresDatabase() throws Exception {
        String password = System.getenv()
                .getOrDefault("POSTGRES_PASSWORD", "chess_local_dev");

        PostgresConnection postgresConnection = new PostgresConnection(
                "jdbc:postgresql://localhost:5432/chess",
                "chess_user",
                password
        );

        try (Connection connection = postgresConnection.open()) {
            assertNotNull(connection);
        }
    }
}
