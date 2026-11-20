package com.omar.rdv.activite;

import com.omar.rdv.prestataire.Prestataire;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activite")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestataire_id", nullable = false, unique = true)
    private Prestataire prestataire;

    @Setter
    @Column(nullable = false, length = 100)
    private String nom;

    @Setter
    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Setter
    @Column(nullable = false, length = 20)
    private String telephone;

    @Setter
    private String adresse;

    @Setter
    @Column(name = "fuseau_horaire", nullable = false, length = 50)
    private String fuseauHoraire;

    @Setter
    @Column(nullable = false, length = 3)
    private String devise;

    @Setter
    @Column(name = "delai_minimum_reservation_minutes", nullable = false)
    private int delaiMinimumReservationMinutes = 60;

    @Setter
    @Column(name = "horizon_reservation_jours", nullable = false)
    private int horizonReservationJours = 60;

    @Setter
    @Column(name = "pas_creneaux_minutes", nullable = false)
    private int pasCreneauxMinutes = 15;

    @Setter
    @Column(name = "delai_annulation_heures", nullable = false)
    private int delaiAnnulationHeures = 24;

    public Activite(Prestataire prestataire, String nom, String slug, String telephone,
                    String fuseauHoraire, String devise) {
        this.prestataire = prestataire;
        this.nom = nom;
        this.slug = slug;
        this.telephone = telephone;
        this.fuseauHoraire = fuseauHoraire;
        this.devise = devise;
    }
}