package org.example.Service;

import org.example.Model.Student;
import org.example.Model.Prof;
import org.example.Model.Parents;
import org.example.App.DatabaseConnection;
import org.example.Model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final Connection cnx;

    public UserService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public void ajouter(User user) {
        String req = "INSERT INTO user (email, password, roles, first_name, last_name, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getPassword());
            stm.setString(3, String.join(",", user.getRoles()));
            stm.setString(4, user.getFirstName());
            stm.setString(5, user.getLastName());
            stm.setString(6, user.getType());

            int rowsAffected = stm.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // <---- 🧨 Add this!
            throw new RuntimeException("Error adding user to the database.", e);
        }
    }

    // Update an existing User
    public void update(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("Cannot update user with null ID.");
        }

        String req = "UPDATE users SET email = ?, password = ?, roles = ?, first_name = ?, last_name = ?, type = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getPassword());
            stm.setString(3, String.join(",", user.getRoles())); // Convert roles list to a comma-separated string
            stm.setString(4, user.getFirstName());
            stm.setString(5, user.getLastName());
            stm.setString(6, user.getType());
            stm.setLong(7, user.getId());

            int rowsUpdated = stm.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("⚠️ No user updated. Check if the ID exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user in the database.", e);
        }
    }

    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        List.of(rs.getString("roles").split(",")),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("type")
                );
                user.setId(rs.getLong("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users from the database.", e);
        }
        return users;
    }

    // Get a specific user by ID
    public User getUserById(Long userId) {
        User user = null;
        String req = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, userId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        List.of(rs.getString("roles").split(",")),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("type")
                );
                user.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user with ID: " + userId, e);
        }
        return user;
    }

    // Delete a user by ID
    public void supprimer(Long id) {
        String req = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user with ID: " + id, e);
        }
    }

    // Assign relationships to the user (Student, Prof, Parents)
    public void assignRelationshipToUser(Long userId, Student student, Prof prof, Parents parent) {
        String updateUserQuery = "UPDATE users SET type = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(updateUserQuery)) {
            String type = null;
            if (student != null) {
                type = "student";
            } else if (prof != null) {
                type = "prof";
            } else if (parent != null) {
                type = "parent";
            }
            stm.setString(1, type);
            stm.setLong(2, userId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning relationship to user.", e);
        }
    }

    public User authenticate(String email, String password) {
        String req = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, email);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        List.of(rs.getString("roles").split(",")),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("type")
                );
                user.setId(rs.getLong("id"));
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during authentication.", e);
        }
        return null; // If no match
    }
}

