package fr.lajusticiarugliano.jeecardgames;

import fr.lajusticiarugliano.jeecardgames.entities.AppUser;
import fr.lajusticiarugliano.jeecardgames.entities.Game;
import fr.lajusticiarugliano.jeecardgames.services.GameService;
import fr.lajusticiarugliano.jeecardgames.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class JeeCardGamesApplication {

	public static void main(String[] args) {
		SpringApplication.run(JeeCardGamesApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//temporaire, permet d'ajouter des données à la BDD
	@Bean
	CommandLineRunner runUserService(UserService service) {
		return args -> {
			service.saveUser(new AppUser(null, "evan", "evan@mail.com", "evan_pass", "ROLE_ADMIN", new ArrayList<>()));
			service.saveUser(new AppUser(null, "pauline", "pauline@mail.com", "pauline_pass", "ROLE_ADMIN", new ArrayList<>()));
			service.saveUser(new AppUser(null, "noob", "noob@mail.com", "noob_pass", "ROLE_USER", new ArrayList<>()));
		};
	}

	@Bean
	CommandLineRunner runGameService(GameService service) {
		return args -> {
			service.addGame(new Game(null, "Black Jack", true));
			service.addGame(new Game(null, "War", true));
			service.addGame(new Game(null, "Memory", false));
		};
	}

}
