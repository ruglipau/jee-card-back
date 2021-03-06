package fr.lajusticiarugliano.jeecardgames.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collection;

@Entity(name="User")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 16)
    private String username;

    @Column(nullable = false, length = 255, unique = true)
    @Email
    private String mail;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 32)
    private String role;

    @OneToMany(fetch = FetchType.EAGER)
    private Collection<GameSummary> gameSummaries = new ArrayList<>();
}
