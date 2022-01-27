package fr.lajusticiarugliano.jeecardgames.controllers;

import fr.lajusticiarugliano.jeecardgames.entities.AppUser;
import fr.lajusticiarugliano.jeecardgames.entities.Game;
import fr.lajusticiarugliano.jeecardgames.models.GameAvailabilityDTO;
import fr.lajusticiarugliano.jeecardgames.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    @GetMapping("")
    public ResponseEntity<List<Game>> getUsers() {
        return ResponseEntity.ok().body(gameService.getGames());
    }

    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkAvailable(@RequestParam String name) {
        return ResponseEntity.ok().body(gameService.getGame(name).isAvailable());
    }

    @PostMapping("availability")
    public ResponseEntity<Game> setAvailable(@RequestBody GameAvailabilityDTO dto) {
        gameService.setAvailable(dto.getGameId(), dto.isAvailable());
        return ResponseEntity.ok().body(gameService.getGame(dto.getGameId()));
    }
}
