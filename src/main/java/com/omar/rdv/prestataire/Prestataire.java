package com.omar.rdv.prestataire;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Locale;

@Getter
@Entity
@Table(name = "prestataire")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prestataire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 100)
    private String nom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 100)
    private String motDePasse;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Instant dateCreation;


    public Prestataire(String nom, String email, String motDePasse) {
        this.nom = nom;
        this.email = normaliserEmail(email);
        this.motDePasse = motDePasse;
    }

    public static String normaliserEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
