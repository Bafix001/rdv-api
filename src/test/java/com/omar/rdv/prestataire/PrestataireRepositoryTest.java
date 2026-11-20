package com.omar.rdv.prestataire;

import com.omar.rdv.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ActiveProfiles("test")
class PrestataireRepositoryTest {

    @Autowired
    private PrestataireRepository prestataireRepository;

    @Test
    void refuseUnEmailDejaUtilise() throws Exception {
        //Given
        Prestataire awa = new Prestataire("awa", "awa@mail.com", "motdepasse");
        prestataireRepository.saveAndFlush(awa);

        //When
        assertThrows(DataIntegrityViolationException.class, () -> prestataireRepository.saveAndFlush(new Prestataire("awa bis", "Awa@mail.com", "autre")));
    }


}