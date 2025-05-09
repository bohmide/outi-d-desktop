package Controller;

import dao.QuizKidsDAO;
import entities.QuizKids;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class QuizKidsController {

    @FXML private TableView<QuizKids> quizTableView;
    @FXML private TableColumn<QuizKids, String> questionColumn;
    @FXML private TableColumn<QuizKids, Integer> scoreColumn;
    @FXML private TableColumn<QuizKids, String> countryColumn;
    @FXML private TableColumn<QuizKids, String> genreColumn;
    @FXML private TableColumn<QuizKids, Void> actionColumn;

    @FXML private TextField questionField;
    @FXML private TextField optionsField;
    @FXML private TextField correctAnswerField;
    @FXML private TextField mediaField;
    @FXML
    private ComboBox<String> levelComboBox;
    @FXML private TextField scoreField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private ComboBox<String> countryComboBox;

    private ObservableList<String> countries;
    private ObservableList<QuizKids> quizList = FXCollections.observableArrayList();
    private final QuizKidsDAO quizDAO = new QuizKidsDAO();
    private QuizKids selectedQuiz = null;

    @FXML
    private void handleLevelChange() {
        String selectedLevel = levelComboBox.getValue();
        System.out.println("Selected Level: " + selectedLevel);

        // You can now use the selected level in your logic
        // For example, you might store it in the entity or use it for filtering
    }
    @FXML
    private void handleGenreChange() {
        String selectedLevel = genreComboBox.getValue();
        System.out.println("Selected Genre: " + selectedLevel);

        // You can now use the selected level in your logic
        // For example, you might store it in the entity or use it for filtering
    }

    @FXML
    private void handleChooseFile(ActionEvent event) {
        // Implement your file chooser logic here
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            mediaField.setText(selectedFile.getAbsolutePath());  // Assuming mediaField is the TextField where file path should be displayed
        }
    }


    @FXML
    public void initialize() {
        levelComboBox.getSelectionModel().select("Medium");
        genreComboBox.getSelectionModel().select("Science");
        countries = FXCollections.observableArrayList(
                "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Argentina", "Australia", "Austria",
                "Belgium", "Brazil", "Canada", "China", "France", "Germany", "India", "Italy", "Japan", "Mexico",
                "Netherlands", "Russia", "South Korea", "Spain", "United Kingdom", "United States", "Zimbabwe","Tunisia"
        );
        countries.remove("Israel");
        countryComboBox.setItems(countries);

        // Set up TableView columns
        questionColumn.setCellValueFactory(cellData -> cellData.getValue().questionProperty());
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());

        // Set up action column (buttons)
        actionColumn.setCellFactory(param -> new TableCell<QuizKids, Void>() {
            private final Button modifyButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox hbox = new HBox(10, modifyButton, deleteButton);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    QuizKids quiz = getTableView().getItems().get(getIndex());

                    // Action for modifying quiz
                    modifyButton.setOnAction(event -> fillFieldsForEdit(quiz));

                    // Action for deleting quiz
                    deleteButton.setOnAction(event -> {
                        quizDAO.delete(quiz);
                        loadQuizList();
                        clearFields();
                        selectedQuiz = null;
                        showAlert("Supprimé", "Quiz supprimé avec succès !");
                    });

                    setGraphic(hbox);
                }
            }
        });

        // Load the quiz list from the database
        loadQuizList();
        quizTableView.setItems(quizList);
    }

    // Method to load all quizzes into the table
    private void loadQuizList() {
        List<QuizKids> quizzes = quizDAO.getAll();
        quizList.setAll(quizzes);
    }

    @FXML
    private void handleAddQuiz() {
        try {
            QuizKids quiz = new QuizKids(
                    questionField.getText(),
                    List.of(optionsField.getText().split(",")),
                    correctAnswerField.getText(),
                    mediaField.getText(),
                    levelComboBox.getValue(),
                    Integer.parseInt(scoreField.getText()),
                    genreComboBox.getValue(),
                    countryComboBox.getValue()
            );

            quizDAO.insert(quiz);
            loadQuizList();
            clearFields();
            showAlert("Succès", "Quiz ajouté avec succès !");
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez vérifier les champs");
        }
    }

    @FXML
    private void handleUpdateQuiz() {
        if (selectedQuiz == null) {
            showAlert("Erreur", "Aucun quiz sélectionné !");
            return;
        }

        try {
            selectedQuiz.setQuestion(questionField.getText());
            selectedQuiz.setOptions(List.of(optionsField.getText().split(",")));
            selectedQuiz.setCorrectAnswer(correctAnswerField.getText());
            selectedQuiz.setMediaPath(mediaField.getText());
            selectedQuiz.setLevel(levelComboBox.getValue());
            selectedQuiz.setScore(Integer.parseInt(scoreField.getText()));
            selectedQuiz.setGenre(genreComboBox.getValue());
            selectedQuiz.setCountry(countryComboBox.getValue());

            quizDAO.update(selectedQuiz);
            loadQuizList();
            clearFields();
            selectedQuiz = null;
            showAlert("Succès", "Quiz mis à jour !");
        } catch (Exception e) {
            showAlert("Erreur", "Veuillez vérifier les champs !");
        }
    }

    private void fillFieldsForEdit(QuizKids quiz) {
        selectedQuiz = quiz;
        questionField.setText(quiz.getQuestion());
        optionsField.setText(String.join(",", quiz.getOptions()));
        correctAnswerField.setText(quiz.getCorrectAnswer());
        mediaField.setText(quiz.getMediaPath());
        levelComboBox.setValue(quiz.getLevel());
        scoreField.setText(String.valueOf(quiz.getScore()));
        genreComboBox.setValue(quiz.getGenre());
        countryComboBox.setValue(quiz.getCountry());
    }

    private void clearFields() {
        questionField.clear();
        optionsField.clear();
        correctAnswerField.clear();
        mediaField.clear();
        scoreField.clear();

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDeleteQuiz() {
        if (selectedQuiz != null) {
            quizDAO.delete(selectedQuiz);
            loadQuizList();
            clearFields();
            showAlert("Supprimé", "Quiz supprimé !");
        } else {
            showAlert("Erreur", "Sélectionnez un quiz à supprimer !");
        }
    }
}
