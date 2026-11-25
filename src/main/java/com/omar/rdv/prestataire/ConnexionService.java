package com.omar.rdv.prestataire;

import com.omar.rdv.securite.GenerateurJeton;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConnexionService {

    private final PrestataireRepository prestataireRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenerateurJeton generateurJeton;

    @Transactional(readOnly = true)
    public ConnexionResponse connecter(ConnexionRequest request) {

        String email = Prestataire.normaliserEmail(request.email());

        Prestataire prestataire = prestataireRepository.findByEmail(email)
                .orElseThrow(IdentifiantsInvalidesException::new);

        if (!passwordEncoder.matches(request.motDePasse(), prestataire.getMotDePasse())) {
            throw new IdentifiantsInvalidesException();
        }

        return new ConnexionResponse(
                generateurJeton.generer(prestataire),
                generateurJeton.dureeEnSecondes()
        );
    }
}
