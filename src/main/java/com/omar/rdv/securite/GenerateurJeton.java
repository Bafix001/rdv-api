package com.omar.rdv.securite;

import com.omar.rdv.prestataire.Prestataire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class GenerateurJeton {
    private final JwtEncoder jwtEncoder;
    private final Duration duree;

    public GenerateurJeton(JwtEncoder jwtEncoder, @Value("${rdv.jwt.duree}") Duration duree) {
        this.jwtEncoder = jwtEncoder;
        this.duree = duree;
    }

    public String generer(Prestataire prestataire){
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("rdv")
                .issuedAt(now)
                .expiresAt(now.plus(duree))
                .subject(prestataire.getId().toString())
                .build();

        JwsHeader entete = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(entete, claims)).getTokenValue();
    }

    public long dureeEnSecondes() {
        return duree.toSeconds();
    }
}
