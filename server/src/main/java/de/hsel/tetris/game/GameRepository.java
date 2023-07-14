package de.hsel.tetris.game;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Integer> {
    Optional<Game> findById(Integer id);
    List<Game> findAllByIsOpen(Boolean open);
    List<Game> findAllByIsRunning(Boolean open);
    List<Game> findAllByOwnerID(Integer id);
    List<Game> findAllByMaxPlayers(Integer maxPlayers);

    @Query(value ="SELECT player FROM game_players WHERE game_id = ?1", nativeQuery = true)
    List<Integer> findAllPlayersByGameID(Integer id);
}
