package controllers;

import services.QuizService;
import entities.Chapitres;
import entities.Cours;
import entities.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class QuizControllerProf {

    @FXML
    private Label lblChapitreInfo;

    @FXML
    private TableView<Quiz> tableQuiz;
    @FXML
    private TableColumn<Quiz, Number> colId;
    @FXML
    private TableColumn<Quiz, String> colNomQuiz;
    @FXML
    private TableColumn<Quiz, Void> colActionQuestions;
    @FXML
    private TableColumn<Quiz, Void> colAction;

    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnRetour;
    @FXML
    private TextField tfNomQuiz;

    private final QuizService quizService = new QuizService();
    private ObservableList<Quiz> quizList = FXCollections.observableArrayList();
    private Quiz selectedQuiz;
    private Chapitres currentChapitre;
    private Cours currentCours;

    public void initData(Chapitres chapitre, Cours cours) {
        this.currentChapitre = chapitre;
        this.currentCours = cours;
        lblChapitreInfo.setText("Quiz du chapitre: " + chapitre.getNomChapitre() + " | Cours: " + cours.getNom());
        loadQuiz();
        updateButtonStates();
    }

    @FXML
    public void initialize() {
        colNomQuiz.setCellValueFactory(new PropertyValueFactory<>("titre"));
        addQuestionsButtonToTable();
        addActionButtonsToTable();
        tableQuiz.setOnMouseClicked(this::handleRowClick);
    }

    private void loadQuiz() {
        try {
            if (currentChapitre != null) {
                List<Quiz> quizzes = quizService.getAllQuizzes().stream()
                        .filter(q -> q.getChapitre() != null && q.getChapitre().getId() == currentChapitre.getId())
                        .collect(Collectors.toList());

                quizList.setAll(quizzes);
                tableQuiz.setItems(quizList);
                updateButtonStates();
            }
        } catch (SQLException e) {
            showErrorAlert("Erreur lors du chargement des quizzes", e);
            quizList.clear();
            updateButtonStates();
        }
    }


    private void updateButtonStates() {
        boolean hasQuiz = !quizList.isEmpty();
        boolean hasSelection = selectedQuiz != null;

        btnAjouter.setDisable(hasQuiz);
        btnModifier.setDisable(!hasSelection);
        btnSupprimer.setDisable(!hasSelection);
    }

    private void addQuestionsButtonToTable() {
        colActionQuestions.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Gérer Questions");

            {
                btn.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    navigateToQuestions(quiz);
                });
                btn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }
    private void addActionButtonsToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final HBox container = new HBox(5);
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");

            {
                btnEdit.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    selectQuizForEdit(quiz);
                });

                btnDelete.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    tableQuiz.getSelectionModel().select(quiz);
                    supprimerQuiz(event);
                });

                btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                container.getChildren().addAll(btnEdit, btnDelete);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void navigateToQuestions(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/test/QuestionView.fxml"));
            Parent root = loader.load();

            QuestionControllerProf controller = loader.getController();
            controller.initData(quiz, currentChapitre, currentCours);

            Scene scene = tableQuiz.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Erreur lors du chargement de la vue des questions", e);
        }
    }

    @FXML
    private void ajouterQuiz() {
        String nom = tfNomQuiz.getText().trim();

        if (!validateQuizTitle(nom)) {
            return;
        }

        try {
            Quiz quiz = new Quiz();
            quiz.setTitre(nom);
            quiz.setScore(0);
            quiz.setChapitre(currentChapitre);

            quizService.ajouterQuiz(quiz);
            loadQuiz();
            clearForm();
        } catch (SQLException e) {
            if (e.getMessage().contains("unique constraint") || e.getMessage().contains("duplicate entry")) {
                showAlert("Ce chapitre a déjà un quiz associé");
            } else {
                showErrorAlert("Erreur lors de l'ajout du quiz", e);
            }
        } catch (IllegalStateException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML
    private void modifierQuiz() {
        if (selectedQuiz == null) {
            showAlert("Veuillez sélectionner un quiz à modifier.");
            return;
        }

        String nom = tfNomQuiz.getText().trim();

        if (!validateQuizTitle(nom)) {
            return;
        }

        try {
            selectedQuiz.setTitre(nom);
            quizService.updateQuiz(selectedQuiz);
            loadQuiz();
            clearForm();
        } catch (SQLException e) {
            showErrorAlert("Erreur lors de la modification du quiz", e);
        }
    }
    @FXML
    private void supprimerQuiz(ActionEvent event) {
        Quiz selectedQuiz = tableQuiz.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert("Veuillez sélectionner un quiz à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer ce quiz?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    quizService.supprimerQuiz(selectedQuiz.getId());
                    loadQuiz();
                    clearForm();
                } catch (SQLException e) {
                    showErrorAlert("Erreur lors de la suppression du quiz", e);
                }
            }
        });
    }

    private boolean validateQuizTitle(String title) {
        if (title.isEmpty()) {
            showAlert("Veuillez saisir un titre pour le quiz");
            return false;
        }

        if (title.length() < 3 || title.length() > 50) {
            showAlert("Le titre du quiz doit contenir entre 3 et 50 caractères.");
            return false;
        }

        return true;
    }


    @FXML
    private void retourAuxChapitres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/test/ChapitreView.fxml"));
            Parent root = loader.load();

            ChapitreControllerProf controller = loader.getController();
            controller.initData(currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Erreur lors du retour aux chapitres", e);
        }
    }

    private void selectQuizForEdit(Quiz quiz) {
        selectedQuiz = quiz;
        tfNomQuiz.setText(quiz.getTitre());
        updateButtonStates();
    }

    private void handleRowClick(MouseEvent event) {
        selectedQuiz = tableQuiz.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            tfNomQuiz.setText(selectedQuiz.getTitre());
        }
        updateButtonStates();
    }

    private void clearForm() {
        selectedQuiz = null;
        tfNomQuiz.clear();
        updateButtonStates();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}