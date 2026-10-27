package software.ulpgc.chess.ports;

import  software.ulpgc.chess.domain.Game;

import java.util.Optional;

public interface GameRepository {

    void save(Game game);

    Optional<Game> findByid(String gameId);
}
