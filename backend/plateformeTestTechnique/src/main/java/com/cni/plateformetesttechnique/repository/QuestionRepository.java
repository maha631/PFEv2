package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.NiveauQuestion;
import com.cni.plateformetesttechnique.model.Question;
import com.cni.plateformetesttechnique.model.TypeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findById(Long id);

    List<Question> findByType(TypeQuestion type);
    long countByNiveau(NiveauQuestion niveau);

    Optional<Question> findByEnonce(String enonce);

    List<Question> findByTypeAndNiveauAndIdNotAndTechnologie(
            TypeQuestion type,
            NiveauQuestion niveau,
            Long id,
            String technologie
    );
}