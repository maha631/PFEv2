package com.cni.plateformetesttechnique.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.service.DeveloppeurService;

@RestController
@RequestMapping("/api/developpeurs")
@CrossOrigin(origins = "*")
public class DeveloppeurController {

    @Autowired
    private DeveloppeurService developpeurService;

    @GetMapping
    public List<Developpeur> getAllDeveloppeurs() {
        return developpeurService.getAllDeveloppeurs();
    }
    
    @GetMapping("/non-assignes")
    public ResponseEntity<List<Developpeur>> getDeveloppeursNonAssignes() {
        List<Developpeur> nonAssignes = developpeurService.getDeveloppeursNonAssignes();
        return ResponseEntity.ok(nonAssignes);
    }
    @GetMapping("/{devId}/is-assigned")
    public ResponseEntity<Boolean> isAssigned(@PathVariable(name = "devId")  Long devId) {
        boolean assigned = developpeurService.isAssigned(devId);
        return ResponseEntity.ok(assigned);
    }
    


}

