package com.omar.rdv.prestataire;

public class IdentifiantsInvalidesException extends RuntimeException {
    public IdentifiantsInvalidesException() {
        super("Email ou mot de passe incorrect");
    }
}
