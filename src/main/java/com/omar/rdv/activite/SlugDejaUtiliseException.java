package com.omar.rdv.activite;

public class SlugDejaUtiliseException extends RuntimeException {
    public SlugDejaUtiliseException(String slug) {
        super("Le slug " + slug + " est déjà utilisé");
    }
}
