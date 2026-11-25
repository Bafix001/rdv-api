package com.omar.rdv.prestataire;

import jakarta.validation.constraints.NotBlank;

public record ConnexionRequest(@NotBlank String email, @NotBlank String motDePasse) {
}
