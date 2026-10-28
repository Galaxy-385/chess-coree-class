
package software.ulpgc.chess.ports;

import software.ulpgc.chess.domain.Game;
import java.util.Optional;

public interface GameRepository {

    void save(String gameId, String roomId, Game game);

    Optional<Game> findById(String gameId);
}
