package fr.lajusticiarugliano.jeecardgames.services;

import fr.lajusticiarugliano.jeecardgames.entities.AppUser;
import fr.lajusticiarugliano.jeecardgames.entities.GameSummary;

import java.util.List;

public interface UserService {

    AppUser saveUser(AppUser user);
    GameSummary saveGameSummary(GameSummary gs);
    void setUserRole(Long userId, String roleName);
    void addGameSummaryToUser(Long userId, Long summaryId);
    AppUser getUser(Long id);
    AppUser getUser(String mail);

    //Ajouter de la pagination ?
    List<AppUser> getUsers();
}
