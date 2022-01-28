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
import fr.lajusticiarugliano.jeecardgames.models.NewUsernameDTO;
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

    @GetMapping("")
    public ResponseEntity<List<UserInfoDTO>> getUsers() {
        List<UserInfoDTO> userInfoList = userService.getUsers();
        return ResponseEntity.ok().body(userInfoList);
    }

    @PostMapping("/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody NewUserDTO user) {
        try {
            AppUser savedUser = userService.saveUser(user);

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

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        AppUser user = userService.getUser(ControllerUtil.getMailFromAuthorizationHeader(authorizationHeader));

        GameSummary gs = userService.saveGameSummary(dto);
        userService.addGameSummaryToUser(user.getId(), gs.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/username")
    public ResponseEntity changeUsername(HttpServletRequest request, HttpServletResponse response, @RequestBody NewUsernameDTO dto) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String mail = ControllerUtil.getMailFromAuthorizationHeader(authorizationHeader);

        try {
            userService.setUserName(mail, dto.getUsername());
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<GameSummary>> getGameHistory(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        AppUser user = userService.getUser(ControllerUtil.getMailFromAuthorizationHeader(authorizationHeader));

        List<GameSummary> gameHistory = new ArrayList<>(user.getGameSummaries());
        Collections.reverse(gameHistory);

        return ResponseEntity.ok().body(gameHistory);
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDTO> getUserInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        AppUser user = userService.getUser(ControllerUtil.getMailFromAuthorizationHeader(authorizationHeader));

        UserInfoDTO userInfos = new UserInfoDTO(user.getUsername(), user.getMail(), user.getRole());

        return ResponseEntity.ok().body(userInfos);
    }

    @GetMapping("/refreshtoken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String mail = ControllerUtil.getMailFromAuthorizationHeader(authorizationHeader);
                System.out.println(mail + " is requesting a new token");

                AppUser user = userService.getUser(mail);

                Algorithm algorithm = SecurityUtil.ALGORITHM;

                String accessToken = JWT.create()
                        .withSubject(user.getMail())
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("role", user.getRole())
                        .sign(algorithm);

                String refreshToken = authorizationHeader.substring(7);

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
