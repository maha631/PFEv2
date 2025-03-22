package com.cni.plateformetesttechnique.repository;

import com.cni.plateformetesttechnique.model.ChefDeProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChefDeProjetRepository extends JpaRepository<ChefDeProjet, Long> {
	
	  boolean existsByUsername(String username);
	    boolean existsByEmail(String email);
}
