package fr.lajusticiarugliano.jeecardgames.repositories;

import fr.lajusticiarugliano.jeecardgames.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {}
