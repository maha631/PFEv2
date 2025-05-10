package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.model.DeveloppeurTestScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeveloppeurTestScoreRepository extends JpaRepository<DeveloppeurTestScore, Long> {
    List<DeveloppeurTestScore> findByDeveloppeur(Developpeur developpeur);
    DeveloppeurTestScore findByDeveloppeurIdAndTestId(Long developpeurId, Long testId);
    DeveloppeurTestScore findFirstByDeveloppeurIdAndTestIdOrderByIdDesc(Long developpeurId, Long testId);
    Optional<DeveloppeurTestScore> findByDeveloppeur_IdAndTest_Id(Long developpeurId, Long testId);
    int countByDeveloppeur_IdAndTest_Id(Long developpeurId, Long testId);
    Optional<DeveloppeurTestScore> findTopByDeveloppeur_IdAndTest_IdOrderByAttemptNumberDesc(Long developpeurId, Long testId);

}

