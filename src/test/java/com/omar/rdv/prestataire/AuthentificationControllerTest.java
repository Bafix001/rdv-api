package com.omar.rdv.prestataire;

import com.jayway.jsonpath.JsonPath;
import com.omar.rdv.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class AuthentificationControllerTest {

    private static final String URL_INSCRIPTION = "/api/v1/prestataire/auth/inscription";
    private static final String URL_CONNEXION = "/api/v1/prestataire/auth/connexion";
    private static final String URL_PROTEGEE = "/api/v1/prestataire/activite";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrestataireRepository prestataireRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    // ================= Inscription =================

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
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());

        inscrire(json("AWA@mail.com", "autre-salon"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(containsString("awa@mail.com")));
    }

    @Test
    void refuseUnSlugDejaUtilise() throws Exception {
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());

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

    // ================= Connexion =================

    @Test
    void connecteUnPrestataire() throws Exception {
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());

        connecter("Awa@Mail.com", "secret1234")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jeton").exists())
                .andExpect(jsonPath("$.expireDans").value(28800));
    }

    @Test
    void refuseUnMauvaisMotDePasse() throws Exception {
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());

        connecter("awa@mail.com", "mauvais-mot-de-passe")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Email ou mot de passe incorrect"));
    }

    @Test
    void refuseUnEmailInconnuAvecLeMemeMessage() throws Exception {
        connecter("inconnu@mail.com", "secret1234")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Email ou mot de passe incorrect"));
    }

    // ================= Routes protégées =================

    @Test
    void refuseUneRouteProtegeeSansJeton() throws Exception {
        mockMvc.perform(get(URL_PROTEGEE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accepteUneRouteProtegeeAvecUnJetonValide() throws Exception {
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());
        String jeton = obtenirJeton("awa@mail.com", "secret1234");

        // 404 et non 401 : le jeton est accepté, la route n'existe simplement pas encore (ticket #19)
        mockMvc.perform(get(URL_PROTEGEE).header("Authorization", "Bearer " + jeton))
                .andExpect(status().isNotFound());
    }

    @Test
    void refuseUnJetonModifie() throws Exception {
        inscrire(json("awa@mail.com", "salon-awa")).andExpect(status().isCreated());
        String jeton = obtenirJeton("awa@mail.com", "secret1234");

        // On change le dernier caractère : la signature ne correspond plus
        String jetonModifie = jeton.substring(0, jeton.length() - 1)
                + (jeton.endsWith("A") ? "B" : "A");

        mockMvc.perform(get(URL_PROTEGEE).header("Authorization", "Bearer " + jetonModifie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refuseUnJetonExpire() throws Exception {
        Instant ilYaDeuxHeures = Instant.now().minus(Duration.ofHours(2));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("rdv-api")
                .issuedAt(ilYaDeuxHeures)
                .expiresAt(ilYaDeuxHeures.plus(Duration.ofHours(1)))   // expiré depuis 1 h
                .subject("1")
                .build();
        JwsHeader entete = JwsHeader.with(MacAlgorithm.HS256).build();
        String jetonExpire = jwtEncoder.encode(JwtEncoderParameters.from(entete, claims)).getTokenValue();

        mockMvc.perform(get(URL_PROTEGEE).header("Authorization", "Bearer " + jetonExpire))
                .andExpect(status().isUnauthorized());
    }

    // ================= Méthodes utilitaires =================

    private ResultActions inscrire(String json) throws Exception {
        return mockMvc.perform(post(URL_INSCRIPTION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions connecter(String email, String motDePasse) throws Exception {
        return mockMvc.perform(post(URL_CONNEXION)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","motDePasse":"%s"}
                        """.formatted(email, motDePasse)));
    }

    private String obtenirJeton(String email, String motDePasse) throws Exception {
        String reponse = connecter(email, motDePasse)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(reponse, "$.jeton");
    }

    private String json(String email, String slug) {
        return """
                {"nom":"Awa Diallo","email":"%s","motDePasse":"secret1234",
                 "nomActivite":"Salon Awa","slug":"%s","telephone":"0600000000",
                 "fuseauHoraire":"Europe/Paris","devise":"EUR"}
                """.formatted(email, slug);
    }
}
