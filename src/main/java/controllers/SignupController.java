package controllers;

import Services.*;
import entities.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.user.PasswordHasher;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class SignupController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField numTelField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Label messageLabel;
    @FXML private ProgressBar strengthBar;
    @FXML private Label strengthLabel;
    @FXML private VBox studentFields;

    @FXML private VBox profFields;

    @FXML private VBox parentFields;

    @FXML private VBox collabFields;

    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<String> sexeComboBox;

    @FXML private DatePicker interDatePicker;
    @FXML private ComboBox<String> interModeComboBox;

    @FXML private TextField firstNameChildField;
    @FXML private TextField lastNameChildField;
    @FXML private DatePicker birthdayPicker;
    @FXML private ComboBox<String> childGenderComboBox;
    @FXML private ComboBox<String> learningDiffComboBox;

    @FXML private TextField nomColField;

    private final UserService userService = new UserService();


    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("student", "prof", "parent","collaborator");
        String countryCode = LocationService.getCountryCode();
        String dialCode = CountryDialCode.getDialCode(countryCode);
        if (!dialCode.isEmpty()) {
            numTelField.setText(dialCode + " ");
        }
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkPasswordStrength();
        });

        // Populate dropdowns
        sexeComboBox.getItems().addAll("Homme", "Femme");
        interModeComboBox.getItems().addAll("online", "face to face");
        childGenderComboBox.getItems().addAll("Male", "Female", "Other");
        learningDiffComboBox.getItems().addAll("yes", "no");

        // Gérer l'affichage dynamique des blocs en fonction du rôle
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isStudent = "student".equalsIgnoreCase(newVal);
            boolean isProf = "prof".equalsIgnoreCase(newVal);
            boolean isParent = "parent".equalsIgnoreCase(newVal);
            boolean isCollaborator = "collaborator".equalsIgnoreCase(newVal);

            studentFields.setVisible(isStudent);
            studentFields.setManaged(isStudent);

            profFields.setVisible(isProf);
            profFields.setManaged(isProf);

            parentFields.setVisible(isParent);
            parentFields.setManaged(isParent);

            collabFields.setVisible(isCollaborator);
            collabFields.setManaged(isCollaborator);
        });

        // Masquer tous les blocs par défaut
        studentFields.setVisible(false);
        studentFields.setManaged(false);
        profFields.setVisible(false);
        profFields.setManaged(false);
        parentFields.setVisible(false);
        parentFields.setManaged(false);
        collabFields.setVisible(false);
        collabFields.setManaged(false);
    }



    @FXML
    private void handleSignup() {
        try {
            System.out.println("Clicked Create Account ✅");

            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String type = typeComboBox.getValue();
            String numTel = numTelField.getText().trim();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()
                    || lastName.isEmpty() || type == null || numTel.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            if (firstName.length() < 3 || firstName.length() > 20 ||
                    lastName.length() < 3 || lastName.length() > 20) {
                messageLabel.setText("First and last name must be 3–20 characters.");
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                messageLabel.setText("❗ Please enter a valid email address.");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/outi-d", "root", "")) {
                String query = "SELECT COUNT(*) FROM user WHERE email = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    messageLabel.setText("❗ This email is already in use.");
                    return;
                }
            }

            if (password.length() < 10) {
                messageLabel.setText("❗ Password must be at least 10 characters.");
                return;
            }

            String fullName = (firstName + lastName).toLowerCase();
            if (password.toLowerCase().contains(fullName)) {
                messageLabel.setText("❗ Password must not contain your full name.");
                return;
            }

            String jsonRoles = "[\"ROLE_" + type.toUpperCase() + "\"]";
            String hashedPassword = PasswordHasher.hashPassword(password);
            User user = new User(email, hashedPassword, List.of(jsonRoles), firstName, lastName, type, numTel);

            userService.ajouter(user);
            System.out.println("✅ User created: " + user.getEmail());
            Session.setCurrentUser(user);

            switch (type) {
                case "student" -> {
                    if (birthDatePicker.getValue() == null || sexeComboBox.getValue() == null) {
                        messageLabel.setText("Please fill all student fields.");
                        return;
                    }

                    LocalDate birthDate = birthDatePicker.getValue();
                    LocalDate minDate = LocalDate.now().minusYears(25);
                    LocalDate maxDate = LocalDate.now().minusYears(12);
                    if (birthDate.isBefore(minDate) || birthDate.isAfter(maxDate)) {
                        messageLabel.setText("Age must be between 12 and 25 years.");
                        return;
                    }

                    Student student = new Student(Date.valueOf(birthDate), sexeComboBox.getValue());
                    student.setId(user.getId());
                    new StudentService().ajouter(student);
                }
                case "prof" -> {
                    if (interDatePicker.getValue() == null || interModeComboBox.getValue() == null) {
                        messageLabel.setText("Please fill all professor fields.");
                        return;
                    }

                    LocalDate interDate = interDatePicker.getValue();
                    if (!interDate.isAfter(LocalDate.now().plusDays(1))) {
                        messageLabel.setText("Intervention date must be at least 2 days from today.");
                        return;
                    }

                    Prof prof = new Prof(Date.valueOf(interDate), interModeComboBox.getValue());
                    prof.setId(user.getId());
                    new ProfService().ajouter(prof);
                }
                case "parent" -> {
                    if (firstNameChildField.getText().isEmpty() || lastNameChildField.getText().isEmpty()
                            || birthdayPicker.getValue() == null || childGenderComboBox.getValue() == null
                            || learningDiffComboBox.getValue() == null) {
                        messageLabel.setText("Please fill all parent fields.");
                        return;
                    }

                    LocalDate birthDate = birthdayPicker.getValue();
                    LocalDate minDate = LocalDate.now().minusYears(12);
                    LocalDate maxDate = LocalDate.now().minusYears(5).minusMonths(6);
                    if (birthDate.isBefore(minDate) || birthDate.isAfter(maxDate)) {
                        messageLabel.setText("Child's age must be between 5.5 and 12 years.");
                        return;
                    }

                    Parents parent = new Parents(
                            Date.valueOf(birthDate),
                            firstNameChildField.getText().trim(),
                            lastNameChildField.getText().trim(),
                            childGenderComboBox.getValue(),
                            learningDiffComboBox.getValue()
                    );
                    parent.setId(user.getId());
                    new ParentsService().ajouter(parent);
                }
                case "collaborator" -> {
                    String nomCol = nomColField.getText().trim();
                    if (nomCol.isEmpty() || nomCol.length() < 4 || nomCol.length() > 20) {
                        messageLabel.setText("Collaborator name must be 4–20 characters.");
                        return;
                    }

                    Collaborator collaborator = new Collaborator(nomCol);
                    collaborator.setId(user.getId());
                    new CollaboratorService().ajouter(collaborator);
                }
            }

            // Redirect to login
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Viewss/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Signup error: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Viewss/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkPasswordStrength() {
        String password = passwordField.getText();
        String result = evaluatePasswordStrength(password);

        switch (result) {
            case "Weak":
                strengthBar.setProgress(0.2);
                strengthBar.setStyle("-fx-accent: red;");
                strengthLabel.setText("Weak");
                break;
            case "Okay":
                strengthBar.setProgress(0.6);
                strengthBar.setStyle("-fx-accent: orange;");
                strengthLabel.setText("Okay");
                break;
            case "Strong":
                strengthBar.setProgress(1.0);
                strengthBar.setStyle("-fx-accent: green;");
                strengthLabel.setText("Strong");
                break;
        }
    }
    private String evaluatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) return "Weak";

        int strength = 0;

        if (password.length() >= 8) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;  // \\d est plus précis pour les chiffres
        if (password.matches(".*[^a-zA-Z0-9].*")) strength++; // caractères spéciaux plus génériques

        switch (strength) {
            case 0: case 1: return "Weak";
            case 2: case 3: return "Okay";
            case 4: case 5: return "Strong";
            default: return "Weak";
        }
    }

}

