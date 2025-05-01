/*package com.cni.plateformetesttechnique.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;

@Service
public class DeveloppeurService {
    
    @Autowired
    private DeveloppeurRepository developpeurRepository;

    public List<Developpeur> getAllDeveloppeurs() {
        return developpeurRepository.findAll();
    }
    public List<Developpeur> getDeveloppeursNonAssignes() {
        return developpeurRepository.findByChefDeProjetIdIsNull();
    }
    public boolean isAssigned(Long devId) {
        return developpeurRepository.existsByIdAndChefDeProjetIsNotNull(devId);
    }
    



}*/
package com.cni.plateformetesttechnique.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DeveloppeurService {
    
    @Autowired
    private DeveloppeurRepository developpeurRepository;

    public List<Developpeur> getAllDeveloppeurs() {
        return developpeurRepository.findAll();
    }

    public List<Developpeur> getDeveloppeursNonAssignes() {
        return developpeurRepository.findByChefDeProjetIdIsNull();
    }

    public boolean isAssigned(Long devId) {
        return developpeurRepository.existsByIdAndChefDeProjetIsNotNull(devId);
    }

    public Developpeur getDeveloppeurById(Long id) {
        return developpeurRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Développeur introuvable avec l'id : " + id));
    }

    public Developpeur updateDeveloppeur(Long id, Developpeur profile) {
        Developpeur existing = developpeurRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Développeur introuvable avec l'id : " + id));
        
        // Adapter ces champs selon le vrai modèle
        existing.setUsername(profile.getUsername());
        existing.setExperience(profile.getExperience());
        existing.setEmail(profile.getEmail());
        existing.setSpecialite(profile.getSpecialite());
        existing.setTechnologies(profile.getTechnologies());
    
        return developpeurRepository.save(existing);
    }
}

