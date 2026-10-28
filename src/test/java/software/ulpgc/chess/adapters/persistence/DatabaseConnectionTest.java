
package software.ulpgc.chess.adapters.persistence;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {

    @Test
    void shouldConnectToPostgreSQL() throws Exception {
        DatabaseConnection database = new DatabaseConnection();

        try (Connection connection = database.connect()) {
            assertTrue(connection.isValid(2));
        }
    }
}
