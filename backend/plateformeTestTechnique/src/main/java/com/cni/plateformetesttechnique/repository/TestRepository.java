package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByCreateurId(Long createurId);

    List<Test> findByStatut(String statut); // Récupérer les tests par statut (BROUILLON, PUBLIE)
    List<Test> findByAccesPublicTrueAndStatutAndDateExpirationIsNullOrDateExpirationAfter(String statut, LocalDateTime dateExpiration);
    List<Test> findByCreateur_IdAndStatut(Long createurId, String statut);

}
