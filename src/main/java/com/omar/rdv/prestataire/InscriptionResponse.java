package com.omar.rdv.prestataire;

public record InscriptionResponse(
        Long id,
        String nom,
        String email,
        String slug

) {
}
