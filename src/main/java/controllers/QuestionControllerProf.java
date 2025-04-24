package controllers;

import Services.QuestionService;
import entities.Chapitres;
import entities.Cours;
import entities.Question;
import entities.Quiz;
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

public class QuestionControllerProf {

    @FXML
    private Label lblQuizInfo;

    @FXML
    private TableView<Question> tableQuestions;
    @FXML
    private TableColumn<Question, Number> colId;
    @FXML
    private TableColumn<Question, String> colQuestion;
    @FXML
    private TableColumn<Question, String> colType;
    @FXML
    private TableColumn<Question, Void> colActionReponses;
    @FXML
    private TableColumn<Question, Void> colAction;

    @FXML
    private TextArea taQuestion;
    @FXML
    private ComboBox<String> cbType;

    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnRetour;

    private final QuestionService questionService = new QuestionService();
    private ObservableList<Question> questionList;
    private Question selectedQuestion;
    private Quiz currentQuiz;
    private Chapitres currentChapitre;
    private Cours currentCours;

    public void initData(Quiz quiz, Chapitres chapitre, Cours cours) {
        this.currentQuiz = quiz;
        this.currentChapitre = chapitre;
        this.currentCours = cours;
        lblQuizInfo.setText("Questions du quiz - Score: " + quiz.getScore() +
                " | Chapitre: " + chapitre.getNomChapitre() +
                " | Cours: " + cours.getNom());
        loadQuestions();
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes
        //colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colQuestion.setCellValueFactory(new PropertyValueFactory<>("question"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Configuration du ComboBox
        cbType.setItems(FXCollections.observableArrayList(
                "Choix multiple", "Vrai/Faux", "Réponse courte", "Réponse longue"
        ));

        // Ajout des boutons d'action
        addReponsesButtonToTable();
//        addActionButtonsToTable();

        // Gestion du clic sur une ligne
        tableQuestions.setOnMouseClicked(this::handleRowClick);
    }

    private void loadQuestions() {
        if (currentQuiz != null) {
            questionList = FXCollections.observableArrayList(questionService.getQuestionsByQuiz(currentQuiz));
            tableQuestions.setItems(questionList);
        }
    }

    private void addReponsesButtonToTable() {
        colActionReponses.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Gérer Réponses");

            {
                btn.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    navigateToReponses(question);
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
                    Question question = getTableView().getItems().get(getIndex());
                    selectQuestionForEdit(question);
                });
                btnDelete.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    supprimerQuestion(question);
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

    private void navigateToReponses(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReponseView.fxml"));
            Parent root = loader.load();

            ReponseControllerProf controller = loader.getController();
            controller.initData(question, currentQuiz, currentChapitre, currentCours);

            Scene scene = tableQuestions.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement de la vue des réponses");
        }
    }

    @FXML
    private void ajouterQuestion() {
        String questionText = taQuestion.getText().trim();
        String type = cbType.getValue();

        if (questionText.isEmpty()) {
            showAlert("Veuillez saisir l'intitulé de la question.");
            return;
        }

        if (questionText.length() < 5 || questionText.length() > 50) {
            showAlert("La question doit contenir entre 5 et 200 caractères.");
            return;
        }

        if (type == null || type.isEmpty()) {
            showAlert("Veuillez sélectionner un type de question.");
            return;
        }

        Question question = new Question();
        question.setQuestion(questionText);
        question.setType(type);
        question.setQuiz(currentQuiz);

        questionService.ajouterQuestion(question);
        loadQuestions();
        clearForm();
    }

    @FXML
    private void modifierQuestion() {
        if (selectedQuestion == null) {
            showAlert("Veuillez sélectionner une question à modifier.");
            return;
        }

        String questionText = taQuestion.getText().trim();
        String type = cbType.getValue();

        if (questionText.isEmpty()) {
            showAlert("La question ne peut pas être vide.");
            return;
        }

        if (questionText.length() < 5 || questionText.length() > 50) {
            showAlert("La question doit contenir entre 5 et 200 caractères.");
            return;
        }

        if (type == null || type.isEmpty()) {
            showAlert("Veuillez sélectionner un type de question.");
            return;
        }
        selectedQuestion.setQuestion(taQuestion.getText());
        selectedQuestion.setType(cbType.getValue());


        questionService.updateQuestion(selectedQuestion);
        loadQuestions();
        clearForm();
    }
    @FXML
    private void supprimerQuestion(javafx.event.ActionEvent event) {
        Question selected = tableQuestions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            questionService.supprimerQuestion(selected.getId());
            loadQuestions(); // recharge la liste
        } else {
            System.out.println("Aucune question sélectionnée.");
        }
    }

    private void supprimerQuestion(Question question) {
        if (question == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette question?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                questionService.supprimerQuestion(question.getId());
                loadQuestions();
                clearForm();
            }
        });
    }

    @FXML
    private void retourAuxQuiz() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuizProfView.fxml"));
            Parent root = loader.load();

            QuizControllerProf controller = loader.getController();
            controller.initData(currentChapitre, currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectQuestionForEdit(Question question) {
        selectedQuestion = question;
        taQuestion.setText(question.getQuestion());
        cbType.setValue(question.getType());
    }

    private void handleRowClick(MouseEvent event) {
        selectedQuestion = tableQuestions.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            taQuestion.setText(selectedQuestion.getQuestion());
            cbType.setValue(selectedQuestion.getType());
        }
    }

    private void clearForm() {
        taQuestion.clear();
        cbType.setValue(null);
        selectedQuestion = null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setContentText(message);
        alert.showAndWait();
    }
}