package controllers;

import Services.QuizService;
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

import javax.swing.*;
import java.io.IOException;

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
    private ObservableList<Quiz> quizList;
    private Quiz selectedQuiz;
    private Chapitres currentChapitre;
    private Cours currentCours;

    public void initData(Chapitres chapitre, Cours cours) {
        this.currentChapitre = chapitre;
        this.currentCours = cours;
        lblChapitreInfo.setText("Quiz du chapitre: " + chapitre.getNomChapitre() + " | Cours: " + cours.getNom());
        loadQuiz();
    }

    @FXML
    public void initialize() {

//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNomQuiz.setCellValueFactory(new PropertyValueFactory<>("titre"));

        addQuestionsButtonToTable();
//        addActionButtonsToTable();

        // Gestion du clic sur une ligne
        tableQuiz.setOnMouseClicked(this::handleRowClick);
    }

    private void loadQuiz() {
        if (currentChapitre != null) {
            quizList = FXCollections.observableArrayList(quizService.getAllQuizzes().stream()
                    .filter(q -> q.getChapitre() != null && q.getChapitre().getId() == currentChapitre.getId())
                    .toList());
            tableQuiz.setItems(quizList);
        }
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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
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
                    supprimerQuiz(quiz);
                });

                btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                container.getChildren().addAll(btnEdit, btnDelete);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void navigateToQuestions(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuestionView.fxml"));
            Parent root = loader.load();

            QuestionControllerProf controller = loader.getController();
            controller.initData(quiz, currentChapitre, currentCours);

            Scene scene = tableQuiz.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement de la vue des questions");
        }
    }

    @FXML
    private void ajouterQuiz() {

        String nom = tfNomQuiz.getText().trim();
        if (nom.isEmpty()) {
            showAlert("Veuillez saisir un titre pour le quiz");
            return;
        }
// Contrôle : longueur
        if (nom.length() < 3 || nom.length() > 10) {
            showAlert("Le titre du quiz doit contenir entre 3 et 50 caractères.");
            return;
        }
        // Contrôle : duplication
        boolean existeDeja = quizList.stream()
                .anyMatch(q -> q.getTitre().equalsIgnoreCase(nom));
        if (existeDeja) {
            showAlert("Un quiz avec ce titre existe déjà pour ce chapitre.");
            return;
        }

        Quiz quiz = new Quiz();
        quiz.setTitre(nom);
        quiz.setScore(0);
        quiz.setChapitre(currentChapitre);

        quizService.ajouterQuiz(quiz);
        loadQuiz();
        clearForm();
    }

    @FXML
    private void modifierQuiz() {
        if (selectedQuiz == null) {
            showAlert("Veuillez sélectionner un quiz à modifier.");
            return;
        }

        String nom = tfNomQuiz.getText().trim();

        if (nom.isEmpty()) {
            showAlert("Le titre du quiz ne peut pas être vide.");
            return;
        }

        if (nom.length() < 3 || nom.length() > 50) {
            showAlert("Le titre du quiz doit contenir entre 3 et 50 caractères.");
            return;
        }

        // Vérifie si un autre quiz du chapitre a déjà ce titre
        boolean existeDeja = quizList.stream()
                .anyMatch(q -> !q.equals(selectedQuiz) && q.getTitre().equalsIgnoreCase(nom));
        if (existeDeja) {
            showAlert("Un autre quiz avec ce titre existe déjà pour ce chapitre.");
            return;
        }

        selectedQuiz.setTitre(nom);
        quizService.updateQuiz(selectedQuiz);
        loadQuiz();
        clearForm();
    }

    private void supprimerQuiz(Quiz quiz) {
        if (quiz == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer ce quiz?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                quizService.supprimerQuiz(quiz.getId());
                loadQuiz();
                clearForm();
            }
        });
    }
    @FXML
    private void supprimerQuiz(ActionEvent event) {
        Quiz selectedQuiz = tableQuiz.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun quiz sélectionné");
            alert.setContentText("Veuillez sélectionner un quiz à supprimer.");
            alert.show();
            return;
        }

        supprimerQuiz(selectedQuiz);
    }

    @FXML
    private void retourAuxChapitres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChapitreView.fxml"));
            Parent root = loader.load();

            ChapitreControllerProf controller = loader.getController();
            controller.initData(currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectQuizForEdit(Quiz quiz) {
        selectedQuiz = quiz;
        tfNomQuiz.setText(quiz.getTitre());
    }

    private void handleRowClick(MouseEvent event) {
        selectedQuiz = tableQuiz.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            tfNomQuiz.setText(selectedQuiz.getTitre());
        }
    }

    private void clearForm() {
        selectedQuiz = null;
        tfNomQuiz.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setContentText(message);
        alert.showAndWait();
    }


}