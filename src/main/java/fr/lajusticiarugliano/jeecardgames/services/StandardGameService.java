package fr.lajusticiarugliano.jeecardgames.services;

import fr.lajusticiarugliano.jeecardgames.entities.Game;
import fr.lajusticiarugliano.jeecardgames.repositories.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StandardGameService implements GameService {

    private final GameRepository gameRepository;

    @Override
    public Game addGame(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public Game getGame(Long id) {
        Optional<Game> game = gameRepository.findById(id);
        if(game.isPresent()) return game.get();
        return null;
    }

    @Override
    public void setAvailable(Long gameId, boolean available) {
        Optional<Game> game = gameRepository.findById(gameId);
        if(game.isPresent()) game.get().setAvailable(available);
    }

    @Override
    public List<Game> getGames() { return gameRepository.findAll(); }
}
