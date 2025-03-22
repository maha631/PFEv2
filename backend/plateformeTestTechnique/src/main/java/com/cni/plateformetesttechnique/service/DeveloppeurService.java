package com.cni.plateformetesttechnique.service;

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



}
