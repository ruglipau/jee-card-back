package fr.lajusticiarugliano.jeecardgames.repositories;

import fr.lajusticiarugliano.jeecardgames.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);
    AppUser findByMail(String mail);
}
