package com.cni.plateformetesttechnique.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Héritage JOINED : chaque sous-classe a sa propre table

public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private TypeQuestion type;

	private String enonce;

	@Enumerated(EnumType.STRING)
	private NiveauQuestion niveau;
	private String technologie; // ex: Java, Python
	private int tempsEstime;    // en minutes
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	@JsonIgnore

	private List<TestQuestion> testQuestions;

	// Constructeur par défaut
	public Question() {}

	// Getters et Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TypeQuestion getType() {
		return type;
	}

	public void setType(TypeQuestion type) {
		this.type = type;
	}

	public String getEnonce() {
		return enonce;
	}

	public void setEnonce(String enonce) {
		this.enonce = enonce;
	}

//	public List<AnswerOption> getAnswerOptions() {
//		return answerOptions;
//	}
//
//	public void setAnswerOptions(List<AnswerOption> answerOptions) {
//		this.answerOptions = answerOptions;
//	}

	public NiveauQuestion getNiveau() {
		return niveau;
	}

	public void setNiveau(NiveauQuestion niveau) {
		this.niveau = niveau;
	}
	public String getTechnologie() {
		return technologie;
	}

	public void setTechnologie(String technologie) {
		this.technologie = technologie;
	}

	public int getTempsEstime() {
		return tempsEstime;
	}

	public void setTempsEstime(int tempsEstime) {
		this.tempsEstime = tempsEstime;
	}
	public List<TestQuestion> getTestQuestions() {
		return testQuestions;
	}

	public void setTestQuestions(List<TestQuestion> testQuestions) {
		this.testQuestions = testQuestions;
	}
}
