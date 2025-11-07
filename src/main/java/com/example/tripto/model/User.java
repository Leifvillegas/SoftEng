package com.example.tripto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String passwordHash;

    @NotBlank
    private String name;

    private Boolean premium = false;

    private String languages;
    private String interests;

    @Min(1) @Max(5)
    private Integer adventurous;

    @Min(1) @Max(5)
    private Integer nightlife;

    @Min(1) @Max(5)
    private Integer budgetFlex;

    @Min(1) @Max(5)
    private Integer planningStyle;

    @Min(1) @Max(5)
    private Integer socialLevel;

    // ---------- Getters and Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public Integer getAdventurous() {
        return adventurous;
    }

    public void setAdventurous(Integer adventurous) {
        this.adventurous = adventurous;
    }

    public Integer getNightlife() {
        return nightlife;
    }

    public void setNightlife(Integer nightlife) {
        this.nightlife = nightlife;
    }

    public Integer getBudgetFlex() {
        return budgetFlex;
    }

    public void setBudgetFlex(Integer budgetFlex) {
        this.budgetFlex = budgetFlex;
    }

    public Integer getPlanningStyle() {
        return planningStyle;
    }

    public void setPlanningStyle(Integer planningStyle) {
        this.planningStyle = planningStyle;
    }

    public Integer getSocialLevel() {
        return socialLevel;
    }

    public void setSocialLevel(Integer socialLevel) {
        this.socialLevel = socialLevel;
    }
}
