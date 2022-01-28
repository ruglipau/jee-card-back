package fr.lajusticiarugliano.jeecardgames.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lajusticiarugliano.jeecardgames.entities.AppUser;
import fr.lajusticiarugliano.jeecardgames.entities.GameSummary;
import fr.lajusticiarugliano.jeecardgames.models.NewGameSummaryDTO;
import fr.lajusticiarugliano.jeecardgames.models.NewUserDTO;
import fr.lajusticiarugliano.jeecardgames.models.UserInfoDTO;
import fr.lajusticiarugliano.jeecardgames.security.SecurityUtil;
import fr.lajusticiarugliano.jeecardgames.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    //private final String MAIL_REGEX = "^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$";

    @GetMapping("")
    public ResponseEntity<List<UserInfoDTO>> getUsers() {

        List<AppUser> userList = userService.getUsers();
        List<UserInfoDTO> userInfoList = new ArrayList<>();

        for (AppUser user : userList) {
            userInfoList.add(new UserInfoDTO(user.getUsername(), user.getMail(), user.getRole()));
        }
        return ResponseEntity.ok().body(userInfoList);
    }

    @PostMapping("/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody NewUserDTO user) {

        /*if(!user.getMail().matches(MAIL_REGEX)) {
            return ResponseEntity.badRequest().body(null);
        }*/

        AppUser appUser = new AppUser(null, user.getUsername(), user.getMail(), user.getPassword(), "ROLE_USER", new ArrayList<>());

        try {
            AppUser savedUser = userService.saveUser(appUser);

            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/save").toUriString());
            return ResponseEntity.created(uri).body(savedUser);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("")
    public ResponseEntity deleteUser(@RequestParam String mail) {
        AppUser user = userService.getUser(mail);
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/newgame")
    public ResponseEntity addGameSummaryToUser(HttpServletRequest request, HttpServletResponse response, @RequestBody NewGameSummaryDTO dto) {
        GameSummary gs = new GameSummary(null, dto.getGame(), dto.isVictory());

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = authorizationHeader.substring(7);

        Algorithm algorithm = SecurityUtil.ALGORITHM;
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(accessToken);

        String mail = decodedJWT.getSubject();

        AppUser user = userService.getUser(mail);

        userService.saveGameSummary(gs);
        userService.addGameSummaryToUser(user.getId(), gs.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<GameSummary>> getGameHistory(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = authorizationHeader.substring(7);

        Algorithm algorithm = SecurityUtil.ALGORITHM;
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(accessToken);

        String mail = decodedJWT.getSubject();

        AppUser user = userService.getUser(mail);

        List<GameSummary> gameHistory = new ArrayList<>(user.getGameSummaries());
        Collections.reverse(gameHistory);

        return ResponseEntity.ok().body(gameHistory);
    }

    @GetMapping("/admins-message")
    public String excluAdmin() {
        return "Ce message est accessible uniquement aux admins";
    }

    @GetMapping("/users-message")
    public String excluUser() {
        return "Ce message est accessible uniquement aux users";
    }

    @GetMapping("auth-message")
    public String excluAuth(HttpServletRequest request, HttpServletResponse response) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = authorizationHeader.substring(7);

        Algorithm algorithm = SecurityUtil.ALGORITHM;
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(accessToken);

        String mail = decodedJWT.getSubject();

        AppUser user = userService.getUser(mail);

        return "Ce message est accessible uniquement aux utilisateurs authentifiés. Vous êtes connecté en tant que " + user.getUsername() + ".";
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDTO> getUserInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = authorizationHeader.substring(7);

        Algorithm algorithm = SecurityUtil.ALGORITHM;
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(accessToken);

        String mail = decodedJWT.getSubject();

        AppUser user = userService.getUser(mail);

        UserInfoDTO userInfos = new UserInfoDTO(user.getUsername(), user.getMail(), user.getRole());

        return ResponseEntity.ok().body(userInfos);
    }

    @GetMapping("hello")
    public String hello() {
        return "Ce message est accessible à tout le monde";
    }

    @GetMapping("/refreshtoken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring(7);

                Algorithm algorithm = SecurityUtil.ALGORITHM;
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);

                String mail = decodedJWT.getSubject();

                System.out.println(mail + " is requesting a new token");

                AppUser user = userService.getUser(mail);

                String accessToken = JWT.create()
                        .withSubject(user.getMail())
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("role", user.getRole())
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<String, String>();

                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> errors = new HashMap<String, String>();
                errors.put("error_message", e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), errors);

            }
        }
        else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
