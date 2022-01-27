package fr.lajusticiarugliano.jeecardgames.services;

import fr.lajusticiarugliano.jeecardgames.entities.Game;

import java.util.List;

public interface GameService {

    Game addGame(Game game);
    Game getGame(Long id);
    Game getGame(String name);
    void setAvailable(Long gameId, boolean available);

    List<Game> getGames();
}
