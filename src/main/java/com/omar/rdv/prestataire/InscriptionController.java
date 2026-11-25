package com.omar.rdv.prestataire;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prestataire/auth")
@RequiredArgsConstructor
public class InscriptionController {
    private final InscriptionService inscriptionService;

    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public InscriptionResponse inscrire(@Valid @RequestBody InscriptionRequest request) {
        return inscriptionService.inscrire(request);
    }
}
