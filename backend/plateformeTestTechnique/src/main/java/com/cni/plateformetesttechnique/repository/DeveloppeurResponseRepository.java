package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.model.Question;
import com.cni.plateformetesttechnique.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cni.plateformetesttechnique.model.DeveloppeurResponse;

import java.util.List;

public interface DeveloppeurResponseRepository extends JpaRepository<DeveloppeurResponse, Long> {
    public DeveloppeurResponse findByDeveloppeurAndTestAndQuestion(Developpeur developpeur, Test test, Question question);

    // Recherche par testId et developpeurId
    List<DeveloppeurResponse> findByTest_IdAndDeveloppeur_Id(Long testId, Long developpeurId);
    int countByTest_IdAndDeveloppeur_Id(Long testId, Long developpeurId);  // Compter les réponses

}

