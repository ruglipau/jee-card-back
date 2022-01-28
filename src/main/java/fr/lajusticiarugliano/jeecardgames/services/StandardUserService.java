package fr.lajusticiarugliano.jeecardgames.services;

import fr.lajusticiarugliano.jeecardgames.entities.AppUser;
import fr.lajusticiarugliano.jeecardgames.entities.GameSummary;
import fr.lajusticiarugliano.jeecardgames.models.NewGameSummaryDTO;
import fr.lajusticiarugliano.jeecardgames.models.NewUserDTO;
import fr.lajusticiarugliano.jeecardgames.models.UserInfoDTO;
import fr.lajusticiarugliano.jeecardgames.repositories.GameSummaryRepository;
import fr.lajusticiarugliano.jeecardgames.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StandardUserService implements UserService, UserDetailsService {

    private final String PASSWORD_REGEX = "(?=.*[a-z])(?=.*[0-9])(?=.*[A-Z]).{8,50}";

    private final UserRepository userRepository;
    private final GameSummaryRepository gameSummaryRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //L'adresse mail est unique et peut servir d'identifiant
        AppUser user = userRepository.findByMail(username);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        System.out.println("loadByUsername");

        //User != AppUser (User est une classe de spring-security)
        return new User(user.getMail(), user.getPassword(), authorities);
    }

    @Override
    public AppUser insertUser(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public AppUser saveUser(NewUserDTO user) throws APIServiceException {
        if(!user.getPassword().matches(PASSWORD_REGEX)) throw new APIServiceException("Bad Password");
        if(user.getUsername().length() < 4) throw new APIServiceException("Username too short");

        AppUser appUser = new AppUser(null, user.getUsername(), user.getMail(), user.getPassword(), "ROLE_USER", new ArrayList<>());

        appUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(appUser);
    }

    @Override
    public GameSummary saveGameSummary(NewGameSummaryDTO dto) {
        GameSummary gs = new GameSummary(null, dto.getGame(), dto.isVictory());
        return gameSummaryRepository.save(gs);
    }

    @Override
    public void setUserRole(Long userId, String roleName) {
        Optional<AppUser> user = userRepository.findById(userId);
        if(user.isPresent()) user.get().setRole(roleName);
    }

    @Override
    public void setUserName(String mail, String username) throws APIServiceException {
        if(username.length() < 4) throw new APIServiceException("Username too short");
        AppUser user = userRepository.findByMail(mail);
        user.setUsername(username);
    }

    @Override
    public void addGameSummaryToUser(Long userId, Long summaryId) {
        Optional<AppUser> optionalUser = userRepository.findById(userId);
        Optional<GameSummary> optionalGS = gameSummaryRepository.findById(summaryId);

        if(optionalUser.isPresent() && optionalGS.isPresent()) optionalUser.get().getGameSummaries().add(optionalGS.get());
    }

    @Override
    public AppUser getUser(Long id) {
        Optional<AppUser> user = userRepository.findById(id);
        if(user.isPresent()) return user.get();
        return null;
    }

    @Override
    public AppUser getUser(String mail) {
        return userRepository.findByMail(mail);
    }

    @Override
    public void deleteUser(AppUser user) {
        userRepository.delete(user);
    }

    @Override
    public List<UserInfoDTO> getUsers() {

        List<AppUser> userList = userRepository.findAll();
        List<UserInfoDTO> userInfoList = new ArrayList<>();

        for (AppUser user : userList) {
            userInfoList.add(new UserInfoDTO(user.getUsername(), user.getMail(), user.getRole()));
        }
        return userInfoList;
    }
}
