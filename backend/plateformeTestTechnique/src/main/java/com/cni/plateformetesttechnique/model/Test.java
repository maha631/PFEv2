package com.cni.plateformetesttechnique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tests")
@AllArgsConstructor
@Builder
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    private Integer duree; // En minutes (NULL = illimité)
    private Integer nbQuestions;       // 10
    private String niveauDifficulte;

    @Column(nullable = false)
    private String type; // QCM, Algo, Mixte... TypeTest

    private Boolean accesPublic; // true = ouvert à tous, false = sur invitation
    private Integer limiteTentatives; // NULL = illimité

    @Column(nullable = false)
    private String statut; // BROUILLON, PUBLIE, ARCHIVE

    private LocalDateTime dateCreation;
    private LocalDateTime dateExpiration; // NULL = pas de date limite
    @ElementCollection
    @CollectionTable(name = "test_technologies", joinColumns = @JoinColumn(name = "test_id"))
    @Column(name = "technology")
    private List<String> technologies; // ex: ["Java", "Python"]

    @ManyToOne
    private Developpeur developpeur;

    @ManyToOne
    @JoinColumn(name = "createur_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

    private User createur;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TestQuestion> testQuestions;
    @Version
    private Integer version;
    // Getters et Setters
    public Test(String titre, String description, int duree, int nbQuestions,
                String niveauDifficulte, String type, boolean accesPublic,
                int limiteTentatives, String statut, LocalDateTime dateCreation,
                LocalDateTime dateExpiration, List<String> technologies, User createur) {
        this.titre = titre;
        this.description = description;
        this.duree = duree;
        this.nbQuestions = nbQuestions;
        this.niveauDifficulte = niveauDifficulte;
        this.type = type;
        this.accesPublic = accesPublic;
        this.limiteTentatives = limiteTentatives;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
        this.technologies = technologies;
        this.createur = createur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getAccesPublic() {
        return accesPublic;
    }

    public void setAccesPublic(Boolean accesPublic) {
        this.accesPublic = accesPublic;
    }

    public Integer getLimiteTentatives() {
        return limiteTentatives;
    }

    public void setLimiteTentatives(Integer limiteTentatives) {
        this.limiteTentatives = limiteTentatives;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    public User getCreateur() {
        return createur;
    }

    public void setCreateur(User createur) {
        this.createur = createur;
    }
    public List<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<String> technologies) {
        this.technologies = technologies;
    }

    public Integer getNbQuestions() {
        return nbQuestions;
    }

    public void setNbQuestions(Integer nbQuestions) {
        this.nbQuestions = nbQuestions;
    }

    public String getNiveauDifficulte() {
        return niveauDifficulte;
    }

    public void setNiveauDifficulte(String niveauDifficulte) {
        this.niveauDifficulte = niveauDifficulte;
    }
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<TestQuestion> getTestQuestions() {
        return testQuestions;
    }

    public void setTestQuestions(List<TestQuestion> testQuestions) {
        this.testQuestions = testQuestions;
    }
    public Test() {
        this.version = 0; // ⚡ Évite le NullPointerException
    }

	public Developpeur getDeveloppeur() {
		return developpeur;
	}

	public void setDeveloppeur(Developpeur developpeur) {
		this.developpeur = developpeur;
	}

}
