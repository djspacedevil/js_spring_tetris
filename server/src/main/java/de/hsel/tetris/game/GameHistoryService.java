package de.hsel.tetris.game;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;

    public GameHistoryService(GameHistoryRepository gameHistoryRepository) {
        this.gameHistoryRepository = gameHistoryRepository;
    }

    public List<Optional<GameHistory>> getUserHistoryById(Integer id) {
        return gameHistoryRepository.findFirst10ByUserIDOrderByIdDesc(id);
    }

    public void addUserHistory(Integer userId, Integer score, Date playDate, String endState) {
        var gameHistory = new GameHistory();
        gameHistory.setUserID(userId);
        gameHistory.setScore(score);
        gameHistory.setPlayDate(playDate);
        gameHistory.setEndState(endState);
        gameHistoryRepository.save(gameHistory);
    }
}
