package com.omar.rdv.activite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {

    boolean existsBySlug(String slug);

    Optional<Activite> findBySlug(String slug);

    Optional<Activite> findByPrestataireId(Long prestataireId);
}
