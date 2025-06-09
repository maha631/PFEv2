package com.cni.plateformetesttechnique.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cni.plateformetesttechnique.model.Certification;
import com.cni.plateformetesttechnique.service.CertificationService;
import com.cni.plateformetesttechnique.repository.CertifRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CertifController {

    @Autowired
    private CertifRepository certificationRepository;

    @Autowired
    private CertificationService certificationService;

    @GetMapping("/certifications/{developpeurId}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<List<Certification>> getCertifications(@PathVariable(name = "developpeurId") Long developpeurId) {


        certificationService.verifierEtAttribuerCertification(developpeurId);


        List<Certification> certifications = certificationRepository.findByDeveloppeurId(developpeurId);

        return ResponseEntity.ok(certifications);
    }
    @GetMapping("/certification/download/{id}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<byte[]> downloadCertification(@PathVariable(name="id") Long id) {
        byte[] pdfBytes = certificationService.generateCertificationPdf(id);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=certification-" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(pdfBytes);
    }

    @GetMapping("/certifications/{id}/share")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<String> shareCertif(@PathVariable(name="id") Long id) {
        String fileUrl = certificationService.generateAndStoreCertificationPdf(id);
        return ResponseEntity.ok(fileUrl);
    }
    @GetMapping("/certifs/{filename:.+}")
    @PreAuthorize("hasRole('ROLE_DEVELOPPEUR')")
    public ResponseEntity<Resource> getCertif(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/certifs").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}