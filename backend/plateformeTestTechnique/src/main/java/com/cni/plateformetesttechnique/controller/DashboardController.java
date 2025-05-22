package com.cni.plateformetesttechnique.controller;


import com.cni.plateformetesttechnique.dto.DeveloppeurDashboardDTO;
import com.cni.plateformetesttechnique.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")

@CrossOrigin(origins = "*") // à adapter selon ton front
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/{developpeurId}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public DeveloppeurDashboardDTO getDashboard(@PathVariable(name="developpeurId") Long developpeurId) {
        return dashboardService.getDashboardByDeveloppeurId(developpeurId);
    }
}
