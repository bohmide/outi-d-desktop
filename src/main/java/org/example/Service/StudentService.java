package org.example.Service;

import org.example.Model.Student;
import org.example.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private final Connection cnx;

    public StudentService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    // Add a new Student
    public void ajouter(Student student) {
        String req = "INSERT INTO students (date_birth, sexe) VALUES (?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setDate(1, student.getDateBirth());
            stm.setString(2, student.getSexe());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding student to the database.", e);
        }
    }

    // Update an existing Student
    public void update(Student student) {
        if (student.getId() == null) {
            throw new RuntimeException("Cannot update student with null ID.");
        }

        String req = "UPDATE students SET date_birth = ?, sexe = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setDate(1, student.getDateBirth());
            stm.setString(2, student.getSexe());
            stm.setLong(3, student.getId());
            int rowsUpdated = stm.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("⚠️ No student updated. Check if the ID exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating student in the database.", e);
        }
    }

    // Get all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String req = "SELECT * FROM students";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                Student student = new Student(
                        rs.getDate("date_birth"),
                        rs.getString("sexe")
                );
                student.setId(rs.getLong("id"));
                students.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving students from the database.", e);
        }
        return students;
    }

    // Get a specific student by ID
    public Student getStudentById(Long studentId) {
        Student student = null;
        String req = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, studentId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                student = new Student(
                        rs.getDate("date_birth"),
                        rs.getString("sexe")
                );
                student.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving student with ID: " + studentId, e);
        }
        return student;
    }

    // Delete a student by ID
    public void supprimer(Long id) {
        String req = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student with ID: " + id, e);
        }
    }
}


