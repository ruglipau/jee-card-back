package fr.lajusticiarugliano.jeecardgames.models;

import lombok.Data;

@Data
public class GameAvailabilityDTO {

    private Long gameId;
    private boolean available;
}
