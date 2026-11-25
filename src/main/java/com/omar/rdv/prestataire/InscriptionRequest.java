package com.omar.rdv.prestataire;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InscriptionRequest(@NotBlank @Size(max = 100) String nom,
                                 @Email @NotBlank @Size(max = 255) String email,
                                 @NotBlank @Size(min = 8, max = 72) String motDePasse,
                                 @NotBlank @Size(max = 100) String nomActivite,
                                 @NotBlank @Size(max = 60) @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Veuillez entrer uniquement des lettres minuscules, des chiffres et des tirets") String slug,
                                 @NotBlank @Size(max = 20) String telephone,
                                 @NotBlank @Size(max = 50) String fuseauHoraire,
                                 @NotBlank @Size(max = 3, min = 3) String devise) {
}
