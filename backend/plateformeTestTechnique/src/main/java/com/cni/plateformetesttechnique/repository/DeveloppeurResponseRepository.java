package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeveloppeurResponseRepository extends JpaRepository<DeveloppeurResponse, Long> {


    // Compter les réponses par testId et developpeurId
//    int countByTest_IdAndDeveloppeur_Id(Long testId, Long developpeurId);
//    List<DeveloppeurResponse> findByTestIdAndDeveloppeurId(Long testId, Long developpeurId);
    List<DeveloppeurResponse> findByDeveloppeurTestScore_Developpeur_IdAndDeveloppeurTestScore_Test_Id(Long developpeurId, Long testId);
    long countByDeveloppeurTestScore_Id(Long developpeurTestScoreId);
    List<DeveloppeurResponse> findByDeveloppeurTestScore_Id(Long developpeurTestScoreId);
    boolean existsByDeveloppeurTestScore_IdAndQuestion_Id(Long developpeurTestScoreId, Long questionId);

    DeveloppeurResponse findByDeveloppeurTestScoreAndQuestion(DeveloppeurTestScore developpeurTestScore, Question question);

	List<DeveloppeurResponse> findByDeveloppeurId(Long developpeurId);
	;

}
