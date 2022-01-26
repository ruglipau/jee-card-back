package fr.lajusticiarugliano.jeecardgames.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity(name="User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 16)
    private String username;

    @Column(nullable = false, length = 255, unique = true)
    private String mail;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 32)
    private String role;

    @OneToMany(fetch = FetchType.EAGER)
    private Collection<GameSummary> gameSummaries = new ArrayList<>();
}
