package com.cni.plateformetesttechnique.controller;

import com.cni.plateformetesttechnique.model.InvitationTest;
import com.cni.plateformetesttechnique.service.InvitationTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invitations")
public class InvitationTestController {

    @Autowired
    private InvitationTestService invitationTestService;
// Ajout dans InvitationTestController

    @GetMapping("/{invitationId}")
    public ResponseEntity<InvitationTest> getInvitationDetails(@PathVariable Long invitationId) {
        InvitationTest invitation = invitationTestService.getInvitationDetails(invitationId);
        return ResponseEntity.ok(invitation);
    }
    @PutMapping("/{invitationId}/respond")
    public ResponseEntity<Map<String, Object>> respondToInvitation(
            @PathVariable Long invitationId,
            @RequestParam boolean accept
    ){
    invitationTestService.respondToInvitation(invitationId, accept);
        Map<String, Object> response = new HashMap<>();
        response.put("message", accept ? "Invitation acceptée !" : "Invitation refusée !");


        return ResponseEntity.ok(response);
    }
    @PostMapping("/{testId}/invite")
    public ResponseEntity<String> inviteDevelopers(@PathVariable Long testId, @RequestBody List<Long> developerIds) {
        invitationTestService.inviteDevelopers(testId, developerIds);
        return ResponseEntity.ok("Invitations envoyées avec succès");
    }
}
