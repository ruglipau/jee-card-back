package fr.lajusticiarugliano.jeecardgames.models;

import lombok.Data;

import javax.persistence.*;

@Data
public class NewUserDTO {
    private String username;
    private String mail;
    private String password;
}
