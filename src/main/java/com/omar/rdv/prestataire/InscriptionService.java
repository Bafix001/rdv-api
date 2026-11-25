package com.omar.rdv.prestataire;

import com.omar.rdv.activite.Activite;
import com.omar.rdv.activite.ActiviteRepository;
import com.omar.rdv.activite.SlugDejaUtiliseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InscriptionService {
    private final PrestataireRepository prestataireRepository;
    private final ActiviteRepository activiteRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public InscriptionResponse inscrire(InscriptionRequest request) {
        Prestataire prestataire = new Prestataire(request.nom(), request.email(), passwordEncoder.encode(request.motDePasse()));

        if (prestataireRepository.existsByEmail(prestataire.getEmail())) {
            throw new EmailDejaUtiliseException(prestataire.getEmail());
        }

        if (activiteRepository.existsBySlug(request.slug())) {
            throw new SlugDejaUtiliseException(request.slug());
        }

        prestataireRepository.save(prestataire);

        Activite activite = new Activite(prestataire, request.nomActivite(), request.slug(), request.telephone(), request.fuseauHoraire(), request.devise());

        activiteRepository.save(activite);

        return new InscriptionResponse(prestataire.getId(), prestataire.getNom(), prestataire.getEmail(), activite.getSlug());
    }
}
