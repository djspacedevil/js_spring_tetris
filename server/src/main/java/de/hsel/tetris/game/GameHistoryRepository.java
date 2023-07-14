package de.hsel.tetris.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameHistoryRepository extends CrudRepository<GameHistory, Integer> {
    List<Optional<GameHistory>> findByUserID(Integer id);
    List<Optional<GameHistory>> findFirst1ByUserIDOrderByIdDesc(Integer id);
    List<Optional<GameHistory>> findFirst10ByUserIDOrderByIdDesc(Integer id);
}
