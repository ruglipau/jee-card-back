package fr.lajusticiarugliano.jeecardgames.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private String username;
    private String mail;
    private String role;
}
