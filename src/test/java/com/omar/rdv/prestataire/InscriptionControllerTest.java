package com.omar.rdv.prestataire;

import com.omar.rdv.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class InscriptionControllerTest {

    private static final String URL = "/api/v1/prestataire/auth/inscription";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrestataireRepository prestataireRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void inscritUnPrestataire() throws Exception {
        inscrire(json("Awa@Mail.com", "salon-awa"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("awa@mail.com"))
                .andExpect(jsonPath("$.slug").value("salon-awa"))
                .andExpect(jsonPath("$.motDePasse").doesNotExist());
    }

    @Test
    void refuseUnEmailDejaUtilise() throws Exception {
        // Given
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());

        // When / Then : même email (avec majuscules), slug différent
        inscrire(json("AWA@mail.com", "autre-salon"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(containsString("awa@mail.com")));
    }

    @Test
    void refuseUnSlugDejaUtilise() throws Exception {
        // Given
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());

        // When / Then : même slug, email différent
        inscrire(json("fatou@mail.com", "salon-awa"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(containsString("salon-awa")));
    }

    @Test
    void refuseDesDonneesInvalides() throws Exception {
        String invalide = """
                {"nom":"Fatou","email":"pas-un-email","motDePasse":"123",
                 "nomActivite":"Salon Fatou","slug":"Mon Salon","telephone":"0600000000",
                 "fuseauHoraire":"Europe/Paris","devise":"EUR"}
                """;

        inscrire(invalide)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreurs.email").exists())
                .andExpect(jsonPath("$.erreurs.motDePasse").exists())
                .andExpect(jsonPath("$.erreurs.slug").exists());
    }

    @Test
    void stockeLeMotDePasseHache() throws Exception {
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());

        Prestataire enBase = prestataireRepository.findByEmail("awa@mail.com").orElseThrow();

        assertNotEquals("secret1234", enBase.getMotDePasse());
        assertTrue(enBase.getMotDePasse().startsWith("$2a$"));
        assertTrue(passwordEncoder.matches("secret1234", enBase.getMotDePasse()));
    }

    // --- Méthodes utilitaires ---

    private ResultActions inscrire(String json) throws Exception {
        return mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private String json(String email, String slug) {
        return """
                {"nom":"Awa Diallo","email":"%s","motDePasse":"secret1234",
                 "nomActivite":"Salon Awa","slug":"%s","telephone":"0600000000",
                 "fuseauHoraire":"Europe/Paris","devise":"EUR"}
                """.formatted(email, slug);
    }
}
