package fr.lajusticiarugliano.jeecardgames.services;

import fr.lajusticiarugliano.jeecardgames.entities.AppUser;
import fr.lajusticiarugliano.jeecardgames.entities.GameSummary;
import fr.lajusticiarugliano.jeecardgames.models.NewGameSummaryDTO;
import fr.lajusticiarugliano.jeecardgames.models.NewUserDTO;
import fr.lajusticiarugliano.jeecardgames.models.UserInfoDTO;

import java.util.List;

public interface UserService {

    AppUser insertUser(AppUser user);
    AppUser saveUser(NewUserDTO user) throws APIServiceException;
    GameSummary saveGameSummary(NewGameSummaryDTO gs);
    void setUserRole(Long userId, String roleName);
    void setUserName(String mail, String username) throws APIServiceException;
    void addGameSummaryToUser(Long userId, Long summaryId);
    AppUser getUser(Long id);
    AppUser getUser(String mail);

    void deleteUser(AppUser user);

    //Ajouter de la pagination ?
    List<UserInfoDTO> getUsers();
}
