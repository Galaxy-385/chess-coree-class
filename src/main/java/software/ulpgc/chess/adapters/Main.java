
package software.ulpgc.chess.adapters;

import javafx.application.Application;
import software.ulpgc.chess.adapters.http.ApiMain;

public class Main {

    public static void main(String[] args) {

        Thread apiThread = new Thread(() -> {
            try {
                ApiMain.main(new String[0]);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        apiThread.setName("chess-api-thread");
        apiThread.setDaemon(false);
        apiThread.start();

        Application.launch(ChessApplication.class, args);
    }
}
