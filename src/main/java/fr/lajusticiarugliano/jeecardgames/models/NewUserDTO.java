package fr.lajusticiarugliano.jeecardgames.models;

import lombok.Data;

import javax.persistence.*;

@Data
public class NewUserDTO {

    @Column(nullable = false, length = 16)
    private String username;

    @Column(nullable = false, length = 255, unique = true)
    private String mail;

    @Column(nullable = false, length = 255)
    private String password;
}
