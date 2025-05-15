package entities.user;

import java.sql.Date;

public class Student {
    private Long id;
    private Date dateBirth;
    private String sexe;

    // ✅ Constructors
    public Student() {}

    public Student(Date dateBirth, String sexe) {
        this.dateBirth = dateBirth;
        this.sexe = sexe;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    // ✅ Helper methods
    public boolean isFemale() {
        return "Femme".equalsIgnoreCase(this.sexe);
    }

    public void setFemale(boolean female) {
        this.sexe = female ? "Femme" : "Homme";
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", dateBirth=" + dateBirth +
                ", sexe='" + sexe + ' ' +
                '}';
    }
}
