package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByCreateurId(Long createurId);

    List<Test> findByStatut(String statut); // Récupérer les tests par statut (BROUILLON, PUBLIE)
    List<Test> findByAccesPublicTrueAndStatutAndDateExpirationIsNullOrDateExpirationAfter(String statut, LocalDateTime dateExpiration);
    List<Test> findByCreateur_IdAndStatut(Long createurId, String statut);
    @Query("SELECT t FROM Test t WHERE :techno MEMBER OF t.technologies AND t.niveauDifficulte = :niveau")
    List<Test> findByTechnologieAndNiveau(@Param("techno") String technologie, @Param("niveau") String niveau);

    long countByStatut(String statut);
    @Query("SELECT t FROM Test t WHERE t.createur.id IN :createurIds AND t.statut = 'PUBLIE'")
    List<Test> findTestsPubliesByCreateurIds(@Param("createurIds") List<Long> createurIds);
    List<Test> findByNiveauDifficulte(String niveauDifficulte);
    long countByNiveauDifficulte(String niveauDifficulte);

    }

