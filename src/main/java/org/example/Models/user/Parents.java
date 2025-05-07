package org.example.Models.user;

import java.sql.Date;

public class Parents {
    private Long id;
    private Date birthdayChild;
    private String firstNameChild;
    private String lastNameChild;
    private String sexeChild;
    private String learningDifficultiesChild;


    // ✅ Constructors
    public Parents() {}

    public Parents(Date birthdayChild, String firstNameChild, String lastNameChild,
                   String sexeChild, String learningDifficultiesChild) {
        this.birthdayChild = birthdayChild;
        this.firstNameChild = firstNameChild;
        this.lastNameChild = lastNameChild;
        this.sexeChild = sexeChild;
        this.learningDifficultiesChild = learningDifficultiesChild;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getBirthdayChild() {
        return birthdayChild;
    }

    public void setBirthdayChild(Date birthdayChild) {
        this.birthdayChild = birthdayChild;
    }

    public String getFirstNameChild() {
        return firstNameChild;
    }

    public void setFirstNameChild(String firstNameChild) {
        this.firstNameChild = firstNameChild;
    }

    public String getLastNameChild() {
        return lastNameChild;
    }

    public void setLastNameChild(String lastNameChild) {
        this.lastNameChild = lastNameChild;
    }

    public String getSexeChild() {
        return sexeChild;
    }
    public void setSexeChild(String sexeChild) {
        this.sexeChild = sexeChild;
    }

    public String getLearningDifficultiesChild() {
        return learningDifficultiesChild;
    }

    public void setLearningDifficultiesChild(String learningDifficultiesChild) {
        this.learningDifficultiesChild = learningDifficultiesChild;
    }


    // ✅ Helper methods
    public boolean hasLearningDifficulties() {
        return "yes".equalsIgnoreCase(this.learningDifficultiesChild);
    }

    public void setLearningDifficulties(boolean hasDifficulty) {
        this.learningDifficultiesChild = hasDifficulty ? "yes" : "no";
    }

    @Override
    public String toString() {
        return "Parents{" +
                "id=" + id +
                ", birthdayChild=" + birthdayChild +
                ", firstNameChild='" + firstNameChild + ' ' +
                ", lastNameChild='" + lastNameChild + ' ' +
                ", sexeChild='" + sexeChild + ' ' +
                ", learningDifficultiesChild='" + learningDifficultiesChild + ' ' +
                '}';
    }
}

