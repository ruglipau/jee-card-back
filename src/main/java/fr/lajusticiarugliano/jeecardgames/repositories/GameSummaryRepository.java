package fr.lajusticiarugliano.jeecardgames.repositories;

import fr.lajusticiarugliano.jeecardgames.entities.GameSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSummaryRepository extends JpaRepository<GameSummary, Long> {}
