package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.Developpeur;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloppeurRepository extends JpaRepository<Developpeur, Long> {

    // Récupérer un développeur par son email
    Optional<Developpeur> findById(Long id);
    @Query("SELECT d FROM Developpeur d WHERE d.email = :email")
    Optional<Developpeur> findByEmail(@Param("email") String email);
     //List<Long> findDeveloppeursBychefDeProjet_id(Long chefDeProjet_id);
    @Query("SELECT d FROM Developpeur d WHERE d.chefDeProjet.id = :chefId")
    List<Developpeur> findByChefDeProjetId(@Param("chefId") Long chefId);
     List<Developpeur> findByChefDeProjetIdIsNull();
     boolean existsByIdAndChefDeProjetIsNotNull(Long id);
	List<Long> findDeveloppeursBychefDeProjet_id(Long chefDeProjet_id);

    @Query("SELECT d.id FROM Developpeur d WHERE d.chefDeProjet.id = :chefId")
    List<Long> findDeveloppeursIdsByChefDeProjetId(@Param("chefId") Long chefId);



}
