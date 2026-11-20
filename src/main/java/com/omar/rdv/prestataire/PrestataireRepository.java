package com.omar.rdv.prestataire;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrestataireRepository extends JpaRepository<Prestataire, Long> {

    boolean existsByEmail(String email);

    Optional<Prestataire> findByEmail(String email);
}
