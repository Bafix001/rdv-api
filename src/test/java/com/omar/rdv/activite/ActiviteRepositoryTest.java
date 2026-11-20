package com.omar.rdv.activite;

import com.omar.rdv.TestcontainersConfiguration;
import com.omar.rdv.prestataire.Prestataire;
import com.omar.rdv.prestataire.PrestataireRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ActiveProfiles("test")
class ActiviteRepositoryTest {

    @Autowired
    private ActiviteRepository activiteRepository;
    @Autowired
    private PrestataireRepository prestataireRepository;

    @Test
    void refuseUnSlugDejaUtilise() {
        //Given
        Prestataire awa = new Prestataire("awa", "awa@gmail.com", "motdepasse");
        prestataireRepository.saveAndFlush(awa);

        Prestataire omar = new Prestataire("omar", "omar@gmail.com", "autre");
        prestataireRepository.saveAndFlush(omar);

        Activite salonAwa = new Activite(awa, "Salon Awa", "salon-beaute", "0600000000", "Europe/Paris", "EUR");
        activiteRepository.saveAndFlush(salonAwa);
        Activite boutiqueOmar = new Activite(omar, "Boutique Omar", "salon-beaute", "0600000000", "Europe/Paris", "EUR");

        //When
        assertThrows(DataIntegrityViolationException.class, () -> activiteRepository.saveAndFlush(boutiqueOmar));

    }


    @Test
    void appliqueLesReglagesParDefaut() {
        //Given
        Prestataire awa = new Prestataire("awa", "awa@gmail.com", "motdepasse");
        prestataireRepository.saveAndFlush(awa);

        Activite salonAwa = new Activite(awa, "Salon Awa", "salon-beaute", "0600000000", "Europe/Paris", "EUR");
        activiteRepository.saveAndFlush(salonAwa);

        //Then
        assertEquals(60, salonAwa.getDelaiMinimumReservationMinutes());
        assertEquals(60, salonAwa.getHorizonReservationJours());
        assertEquals(15, salonAwa.getPasCreneauxMinutes());
        assertEquals(24, salonAwa.getDelaiAnnulationHeures());
    }
}