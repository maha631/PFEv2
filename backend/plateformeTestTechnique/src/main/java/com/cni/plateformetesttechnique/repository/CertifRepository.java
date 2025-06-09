package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.model.Niveau;
import com.cni.plateformetesttechnique.model.Certification;
import com.cni.plateformetesttechnique.model.ChefDeProjet;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertifRepository extends JpaRepository<Certification, Long> {


    List<Certification> findByDeveloppeurId(Long id);
    boolean existsByDeveloppeurAndNiveau(Developpeur developpeur, Niveau niveau);


}