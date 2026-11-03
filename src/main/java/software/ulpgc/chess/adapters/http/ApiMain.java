
package software.ulpgc.chess.adapters.http;

import com.sun.net.httpserver.HttpServer;
import software.ulpgc.chess.adapters.persistence.PostgresConnection;
import software.ulpgc.chess.adapters.persistence.PostgresGameRepository;
import software.ulpgc.chess.adapters.persistence.PostgresRoomRepository;
import software.ulpgc.chess.application.CreateRoom;
import software.ulpgc.chess.application.JoinRoom;
import software.ulpgc.chess.application.GetRoom;
import software.ulpgc.chess.application.PlayMove;


import java.io.IOException;
import java.net.InetSocketAddress;

public class ApiMain {

    public static void main(String[] args) throws IOException {

        String url = System.getenv().getOrDefault(
                "DATABASE_URL",
                "jdbc:postgresql://localhost:5432/chess"
        );

        String user = System.getenv().getOrDefault(
                "DATABASE_USER",
                "chess_user"
        );

        String password = System.getenv().getOrDefault(
                "DATABASE_PASSWORD",
                "chess_local_dev"
        );

        PostgresConnection connection =
                new PostgresConnection(url, user, password);

        PostgresGameRepository gameRepository =
                new PostgresGameRepository(connection);

        PostgresRoomRepository roomRepository =
                new PostgresRoomRepository(connection, gameRepository);

        CreateRoom createRoom = new CreateRoom(roomRepository);
        JoinRoom joinRoom = new JoinRoom(roomRepository);

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        GetRoom getRoom = new GetRoom(roomRepository);

        PlayMove playMove = new PlayMove(roomRepository);

        server.createContext(
                "/rooms",
                new RoomController(createRoom, joinRoom, getRoom, playMove)
        );

        server.start();

        System.out.println("Chess API running at http://localhost:8080");
        System.out.println("Database connection configured.");
    }
}
