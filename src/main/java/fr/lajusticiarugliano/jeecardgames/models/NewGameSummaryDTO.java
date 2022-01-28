package fr.lajusticiarugliano.jeecardgames.models;

import fr.lajusticiarugliano.jeecardgames.entities.GameSummary;
import lombok.Data;

@Data
public class NewGameSummaryDTO {

    private String game;
    private boolean victory;
}
