package controllers;

import services.ReponseService;
import entities.Chapitres;
import entities.Cours;
import entities.Question;
import entities.Quiz;
import entities.Reponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ReponseControllerProf {

    @FXML
    private Label lblQuestionInfo;

    @FXML
    private TableView<Reponse> tableReponses;
    @FXML
    private TableColumn<Reponse, String> colReponse;
    @FXML
    private TableColumn<Reponse, Boolean> colCorrect;
    @FXML
    private TableColumn<Reponse, Void> colAction;

    @FXML
    private TextArea taReponse;
    @FXML
    private CheckBox cbCorrect;

    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnRetour;

    private final ReponseService reponseService = new ReponseService();
    private ObservableList<Reponse> reponseList;
    private Reponse selectedReponse;
    private Question currentQuestion;
    private Quiz currentQuiz;
    private Chapitres currentChapitre;
    private Cours currentCours;

    public void initData(Question question, Quiz quiz, Chapitres chapitre, Cours cours) {
        this.currentQuestion = question;
        this.currentQuiz = quiz;
        this.currentChapitre = chapitre;
        this.currentCours = cours;

        lblQuestionInfo.setText("Réponses pour : " + question.getQuestion() +
                " | Quiz Score : " + quiz.getScore() +
                " | Chapitre : " + chapitre.getNomChapitre() +
                " | Cours : " + cours.getNom());
        loadReponses();
    }

    @FXML
    public void initialize() {
        colReponse.setCellValueFactory(new PropertyValueFactory<>("reponse"));
        colCorrect.setCellValueFactory(new PropertyValueFactory<>("correct"));
//        addActionButtonsToTable();
        tableReponses.setOnMouseClicked(this::handleRowClick);
    }

    private void loadReponses() {
        if (currentQuestion != null) {
            reponseList = FXCollections.observableArrayList(
                    reponseService.getReponsesByQuestion(currentQuestion.getId())
            );
            tableReponses.setItems(reponseList);
        }
    }

    private void addActionButtonsToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final HBox container = new HBox(5);
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");

            {
                btnEdit.setOnAction(event -> {
                    Reponse reponse = getTableView().getItems().get(getIndex());
                    selectReponseForEdit(reponse);
                });
                btnDelete.setOnAction(event -> {
                    Reponse reponse = getTableView().getItems().get(getIndex());
                    supprimerReponse(reponse);
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

    @FXML
    private void ajouterReponse() {
        String reponseText = taReponse.getText().trim();

        if (reponseText.isEmpty()) {
            showAlert("Veuillez saisir le texte de la réponse.");
            return;
        }

        if (reponseText.length() < 2 || reponseText.length() > 100) {
            showAlert("La réponse doit contenir entre 2 et 100 caractères.");
            return;
        }

        boolean estCorrecte = cbCorrect.isSelected();

        if (estCorrecte && isQCU() && alreadyHasCorrectAnswer()) {
            showAlert("En QCU, une seule réponse correcte est autorisée.");
            return;
        }

        Reponse reponse = new Reponse();
        reponse.setReponse(reponseText);
        reponse.setCorrect(estCorrecte);
        reponse.setParentQuestion(currentQuestion);

        reponseService.ajouterReponse(reponse);
        loadReponses();
        clearForm();
    }

    @FXML
    private void modifierReponse() {
        if (selectedReponse == null) {
            showAlert("Veuillez sélectionner une réponse à modifier.");
            return;
        }

        String reponseText = taReponse.getText().trim();

        if (reponseText.isEmpty()) {
            showAlert("La réponse ne peut pas être vide.");
            return;
        }

        if (reponseText.length() < 2 || reponseText.length() > 100) {
            showAlert("La réponse doit contenir entre 2 et 100 caractères.");
            return;
        }

        boolean estCorrecte = cbCorrect.isSelected();

        if (estCorrecte && isQCU() && alreadyHasCorrectAnswer() && !selectedReponse.isCorrect()) {
            showAlert("En QCU, une seule réponse correcte est autorisée.");
            return;
        }

        selectedReponse.setReponse(reponseText);
        selectedReponse.setCorrect(estCorrecte);

        reponseService.updateReponse(selectedReponse);
        loadReponses();
        clearForm();
    }

    private void supprimerReponse(Reponse reponse) {
        if (reponse == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette réponse ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                reponseService.supprimerReponse(reponse.getId());
                loadReponses();
                clearForm();
            }
        });
    }

    @FXML
    public void supprimerReponse() {
        if (selectedReponse == null) {
            showAlert("Veuillez sélectionner une réponse à supprimer.");
            return;
        }
        supprimerReponse(selectedReponse);
    }

    @FXML
    private void retourAuxQuestions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuestionView.fxml"));
            Parent root = loader.load();

            QuestionControllerProf controller = loader.getController();
            controller.initData(currentQuiz, currentChapitre, currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectReponseForEdit(Reponse reponse) {
        selectedReponse = reponse;
        taReponse.setText(reponse.getReponse());
        cbCorrect.setSelected(reponse.isCorrect());
    }

    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            selectedReponse = tableReponses.getSelectionModel().getSelectedItem();
            if (selectedReponse != null) {
                taReponse.setText(selectedReponse.getReponse());
                cbCorrect.setSelected(selectedReponse.isCorrect());
            }
        }
    }

    private void clearForm() {
        taReponse.clear();
        cbCorrect.setSelected(false);
        selectedReponse = null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean alreadyHasCorrectAnswer() {
        return reponseList.stream().anyMatch(Reponse::isCorrect);
    }

    private boolean isQCU() {
        QuestionControllerProf.TypeQuestion typeEnum = QuestionControllerProf.TypeQuestion.valueOf(currentQuestion.getType());
        return QuestionControllerProf.TypeQuestion.valueOf(currentQuestion.getType()) == QuestionControllerProf.TypeQuestion.CHOIX_UNIQUE;

    }
}
