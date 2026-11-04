package software.ulpgc.chess.adapters.launcher;

import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import software.ulpgc.chess.adapters.desktop.ChessApplication;
import software.ulpgc.chess.adapters.http.ApiMain;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        HttpServer apiServer = ApiMain.start();

        try {
            Application.launch(ChessApplication.class, args);
        } finally {
            System.out.println("Closing Chess API...");
            apiServer.stop(1);
            System.out.println("Chess API stopped.");
        }
    }
}