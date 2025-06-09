package com.cni.plateformetesttechnique.service;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cni.plateformetesttechnique.model.Certification;
import com.cni.plateformetesttechnique.model.Developpeur;
import com.cni.plateformetesttechnique.model.DeveloppeurTestScore;
import com.cni.plateformetesttechnique.model.Niveau;
import com.cni.plateformetesttechnique.model.Test;
import com.cni.plateformetesttechnique.repository.CertifRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurRepository;
import com.cni.plateformetesttechnique.repository.DeveloppeurTestScoreRepository;
import com.cni.plateformetesttechnique.repository.TestRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
public class CertificationService {

    @Autowired
    private DeveloppeurRepository developpeurRepository;

    @Autowired
    private DeveloppeurTestScoreRepository developpeurTestScoreRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private CertifRepository certificationRepository;

    public void verifierEtAttribuerCertification(Long developpeurId) {
        System.out.println(">>> Début de verifierEtAttribuerCertification pour développeur ID: " + developpeurId);

        Developpeur developpeur = developpeurRepository.findById(developpeurId)
                .orElseThrow(() -> {
                    System.out.println("!!! Développeur introuvable pour ID: " + developpeurId);
                    return new RuntimeException("Développeur introuvable");
                });

        System.out.println(">>> Développeur trouvé: " + developpeur.getId());

        Map<String, Niveau> niveaux = Map.of(
                "Facile", Niveau.DEBUTANT,
                "Intermédiaire", Niveau.DEVELOPPEUR,
                "Difficile", Niveau.SENIOR
        );

        for (Map.Entry<String, Niveau> entry : niveaux.entrySet()) {
            String niveauCode = entry.getKey();
            Niveau niveauEnum = entry.getValue();

            System.out.println(">>> Vérification pour le niveau: " + niveauCode + " / Enum: " + niveauEnum);

            List<Test> testsParNiveau = testRepository.findByNiveauDifficulte(niveauCode);
            System.out.println(">>> Nombre de tests trouvés pour niveau " + niveauCode + ": " + testsParNiveau.size());

            if (testsParNiveau.isEmpty()) {
                System.out.println(">>> Aucun test pour le niveau " + niveauCode + ", on passe au suivant.");
                continue;
            }

            boolean tousReussis = testsParNiveau.stream().allMatch(test -> {
                System.out.println(">>> Vérification du test ID: " + test.getId());

                Optional<DeveloppeurTestScore> optionalScore =
                        developpeurTestScoreRepository.findTopByDeveloppeur_IdAndTest_IdOrderByAttemptNumberDesc(developpeurId, test.getId());

                if (optionalScore.isPresent()) {
                    double score = optionalScore.get().getScore();
                    System.out.println(">>> Score trouvé: " + score);
                    return score >= 65.0;
                } else {
                    System.out.println(">>> Pas de score trouvé pour test ID: " + test.getId());
                    return false;
                }
            });

            System.out.println(">>> Tous les tests du niveau " + niveauCode + " réussis ? " + tousReussis);

            if (tousReussis && !certificationRepository.existsByDeveloppeurAndNiveau(developpeur, niveauEnum)) {
                System.out.println(">>> Aucune certification existante pour ce niveau, on en crée une.");

                Certification certification = new Certification();
                certification.setDeveloppeur(developpeur);
                certification.setNiveau(niveauEnum);
                certification.setDateObtention(LocalDate.now());
                certificationRepository.save(certification);

                System.out.println(">>> Certification enregistrée pour niveau " + niveauEnum);
            } else {
                System.out.println(">>> Certification déjà existante ou tous les tests pas réussis.");
            }
        }

        System.out.println(">>> Fin de verifierEtAttribuerCertification");
    }
    public byte[] generateCertificationPdf(Long id) {
        Certification certif = certificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification introuvable"));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50); // marges
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Polices
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.DARK_GRAY);
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
            Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
            Font nameFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(0, 102, 204));
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);

            // === HEADER ===
            try {
                URL logoUrl = Objects.requireNonNull(getClass().getClassLoader().getResource("static/logo.png"), "Logo non trouvé");
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(80, 80);
                logo.setAlignment(Image.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception e) {
                Paragraph fallbackLogo = new Paragraph("TechAssess", titleFont);
                fallbackLogo.setAlignment(Element.ALIGN_LEFT);
                document.add(fallbackLogo);
            }

            document.add(Chunk.NEWLINE);

            // === TITRE ===
            Paragraph title = new Paragraph("CERTIFICAT DE COMPÉTENCE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph level = new Paragraph("Niveau : " + certif.getNiveau(), valueFont);
            level.setAlignment(Element.ALIGN_CENTER);
            level.setSpacingAfter(20);
            document.add(level);

            // === INFORMATIONS DU CANDIDAT ===
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(20f);
            table.setWidths(new float[]{40, 60});

            addTableRow(table, "ID Certification :", String.valueOf(certif.getId()), labelFont, valueFont);
            addTableRow(table, "Date d'obtention :", formatDate(certif.getDateObtention()), labelFont, valueFont);
            addTableRow(table, "Nom du candidat :", certif.getDeveloppeur().getUsername(), labelFont, nameFont);
            addTableRow(table, "Spécialité :", certif.getDeveloppeur().getSpecialite(), labelFont, valueFont);

            document.add(table);

            // === QR CODE ===
            try {
                String verificationUrl = "https://techassess.com/verify/" + certif.getId();
                BarcodeQRCode qrCode = new BarcodeQRCode(verificationUrl, 150, 150, null);
                Image qrCodeImage = qrCode.getImage();
                qrCodeImage.setAlignment(Element.ALIGN_CENTER);
                qrCodeImage.scaleToFit(100, 100);
                document.add(qrCodeImage);

                Paragraph qrLabel = new Paragraph("Scannez pour vérifier ce certificat", footerFont);
                qrLabel.setAlignment(Element.ALIGN_CENTER);
                document.add(qrLabel);
            } catch (Exception e) {
                // Ignore QR code if not generated
            }

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // === SIGNATURE ===
            try {
                URL signatureUrl = Objects.requireNonNull(getClass().getClassLoader().getResource("static/signaturre.png"), "Signature non trouvée");
                Image signature = Image.getInstance(signatureUrl);
                signature.scaleToFit(120, 50);
                signature.setAlignment(Image.ALIGN_RIGHT);
                document.add(signature);
            } catch (Exception e) {
                Paragraph signFallback = new Paragraph("Signature : _________________", labelFont);
                signFallback.setAlignment(Element.ALIGN_RIGHT);
                document.add(signFallback);
            }

            Paragraph signTitle = new Paragraph("Directeur des Certifications", valueFont);
            signTitle.setAlignment(Element.ALIGN_RIGHT);
            signTitle.setSpacingBefore(10);
            document.add(signTitle);

            // === PIED DE PAGE ===
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("TechAssess - www.Techassess.com", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF : " + e.getMessage(), e);
        }
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
        cell1.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(value, valueFont));
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell2);
    }

    private String formatDate(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
        return localDate.format(formatter);
    }
    public String generateAndStoreCertificationPdf(Long id) {
        try {
            byte[] pdf = generateCertificationPdf(id);

            String fileName = "certificat_" + id + ".pdf";
            Path path = Paths.get("uploads/certifs/" + fileName);
            Files.createDirectories(path.getParent()); // Crée le dossier s'il n'existe pas
            Files.write(path, pdf);

            // Retourner un lien partageable (ex: pour les réseaux sociaux, email)
            return "http://localhost:8083/certifs/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du certificat PDF : " + e.getMessage(), e);
        }
    }






}