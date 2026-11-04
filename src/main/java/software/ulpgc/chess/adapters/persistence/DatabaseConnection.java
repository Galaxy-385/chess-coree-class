package software.ulpgc.chess.adapters.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/chess";
    private static final String USER = "chess_user";
    private static final String PASSWORD =
            System.getenv().getOrDefault(
                    "POSTGRES_PASSWORD",
                    "chess_local_dev"
            );

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public boolean testConnection(){
        try(Connection connection = connect()){
            return connection.isValid(2);
        }catch (SQLException exception){
            exception.printStackTrace();
            return false;
        }
    }
}
