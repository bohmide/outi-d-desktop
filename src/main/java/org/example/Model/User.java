package org.example.Model;

import java.util.List;

public class User {
    private Long id;
    private String email;
    private String password;
    private List<String> roles;
    private String firstName;
    private String lastName;
    private String type; // can be "student", "prof", "parent"

    // Relations (assuming these based on provided schema)
    private Student student;
    private Prof prof;
    private Parents parent;

    // ✅ Constructors
    public User() {}

    public User(String email, String password, List<String> roles,
                String firstName, String lastName, String type) {
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }

    // ✅ Getters & Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    // ✅ Relationship Getters & Setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Prof getProf() {
        return prof;
    }

    public void setProf(Prof prof) {
        this.prof = prof;
    }

    public Parents getParent() {
        return parent;
    }

    public void setParent(Parents parent) {
        this.parent = parent;
    }

    // ✅ Helper method to check user type
    public boolean isStudent() {
        return "student".equalsIgnoreCase(this.type);
    }

    public boolean isProf() {
        return "prof".equalsIgnoreCase(this.type);
    }

    public boolean isParent() {
        return "parent".equalsIgnoreCase(this.type);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + ' ' +
                ", roles=" + roles +
                ", firstName='" + firstName + ' ' +
                ", lastName='" + lastName + ' ' +
                ", type='" + type + ' ' +
                '}';
    }
}
