package services.user;

import entities.*;
import utils.MyConnection;
import utils.user.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class UserService {
    private final Connection cnx;

    public UserService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    // ✅ Add user with numtel
    public void ajouter(User user) {
        String req = "INSERT INTO user (email, password, roles, first_name, last_name, type, numtel) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getPassword());
            stm.setString(3, String.join(",", user.getRoles()));
            stm.setString(4, user.getFirstName());
            stm.setString(5, user.getLastName());
            stm.setString(6, user.getType());
            stm.setString(7, user.getNumtel());

            int rowsAffected = stm.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding user to the database.", e);
        }
    }

    // ✅ Update user with numtel
    public void update(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("Cannot update user with null ID.");
        }

        String req = "UPDATE user SET email = ?, password = ?, roles = ?, first_name = ?, last_name = ?, type = ?, numtel = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getPassword());
            stm.setString(3, String.join(",", user.getRoles()));
            stm.setString(4, user.getFirstName());
            stm.setString(5, user.getLastName());
            stm.setString(6, user.getType());
            stm.setString(7, user.getNumtel());
            stm.setLong(8, user.getId());

            int rowsUpdated = stm.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("⚠️ No user updated. Check if the ID exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user in the database.", e);
        }
    }

    // ✅ Get all users with numtel
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("password"),
                        List.of(rs.getString("roles").split(",")),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("type"),
                        rs.getString("numtel")
                );
                user.setId(rs.getLong("id"));
                users.add(user); // ✅ fixed
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users from the database.", e);
        }
        return users;
    }

    // ✅ Get user by ID with numtel
    public User getUserById(Long userId) {
        User user = null;
        String req = "SELECT * FROM user WHERE id = ?";
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
                        rs.getString("type"),
                        rs.getString("numtel")
                );
                user.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user with ID: " + userId, e);
        }
        return user;
    }

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setType(rs.getString("type"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users from the database.", e);
        }
        return users;
    }

    public void supprimer(Long id) {
        try (PreparedStatement stm = cnx.prepareStatement("DELETE FROM user WHERE id = ?")) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user with ID " + id, e);
        }
    }

    // 🔗 Assign role relationships
    public void assignRelationshipToUser(Long userId, Student student, Prof prof, Parents parent , Collaborator collaborator) {
        String updateUserQuery = "UPDATE user SET type = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(updateUserQuery)) {
            String type = null;
            if (student != null) type = "student";
            else if (prof != null) type = "prof";
            else if (parent != null) type = "parent";
            else if (collaborator != null) type = "collabortor";

            stm.setString(1, type);
            stm.setLong(2, userId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning relationship to user.", e);
        }
    }

    // ✅ Authenticate with numtel fetched
    public User authenticate(String email, String plainPassword) {
        String req = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (PasswordHasher.checkPassword(plainPassword, hashedPassword)) {
                    User user = new User(
                            rs.getString("email"),
                            hashedPassword,
                            List.of(rs.getString("roles").split(",")),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("type"),
                            rs.getString("numtel")
                    );
                    user.setId(rs.getLong("id"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during authentication.", e);
        }
        return null;
    }

    // Méthode pour obtenir les statistiques (nombre d'utilisateurs par type)
    public Map<String, Integer> getStatistiquesUtilisateursParType(List<User> users) {
        Map<String, Integer> stats = new HashMap<>();
        for (User user : users) {
            String type = user.getType();
            stats.put(type, stats.getOrDefault(type, 0) + 1);
        }
        return stats;
    }

    public User findByEmail(String email) {
        // Si tu as une base de données (avec JDBC par exemple) :
        try {
            String sql = "SELECT * FROM user WHERE email = ?";
            PreparedStatement stmt = cnx.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId((long) rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean checkEmailExists(String email) {
        // Cherche si l'email existe dans la base
        User user = findByEmail(email);
        return user != null;
    }

    public void updatePasswordOnly(String email, String newHashedPassword) {
        String sql = "UPDATE user SET password = ? WHERE email = ?";
        try (PreparedStatement stm = cnx.prepareStatement(sql)) {
            stm.setString(1, newHashedPassword);
            stm.setString(2, email);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("❌ Failed to update password for: " + email, e);
        }
    }
}

