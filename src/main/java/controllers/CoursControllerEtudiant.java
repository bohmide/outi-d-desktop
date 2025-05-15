package controllers;

import entities.Cours;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import services.CoursService;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CoursControllerEtudiant {

    @FXML private GridPane gridCours;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> difficultyFilter;
    @FXML private Pagination pagination;
    @FXML private ComboBox<String> languageCombo;

    private ResourceBundle bundle;
    private final CoursService coursService = new CoursService();
    private ObservableList<Cours> allCourses;
    private FilteredList<Cours> filteredCourses;
    private SortedList<Cours> sortedCourses;
    private static final int ITEMS_PER_PAGE = 6;
    @FXML private Button returnButton;



    @FXML
    public void initialize() {
        // Initialize language
        bundle = ResourceBundle.getBundle("lang/messages", Locale.getDefault());

        // Language selector setup
        languageCombo.getItems().addAll("Français", "English");
        languageCombo.setValue("Français");
        languageCombo.setOnAction(this::changeLanguage);


        // Initialize UI
        loadCourses();
        setupFilters();
        setupSorting();
        updatePagination();

    }

    private void setupFilters() {
        difficultyFilter.getItems().addAll(
                bundle.getString("filter.all"),
                bundle.getString("filter.easy"),
                bundle.getString("filter.medium"),
                bundle.getString("filter.advanced")
        );
        difficultyFilter.setValue(bundle.getString("filter.all"));

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilters());
        difficultyFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilters());
    }

    private void setupSorting() {
        sortComboBox.getItems().addAll(
                bundle.getString("sort.default"),
                bundle.getString("sort.name.asc"),
                bundle.getString("sort.name.desc"),
                bundle.getString("sort.difficulty.asc"),
                bundle.getString("sort.difficulty.desc")
        );
        sortComboBox.setValue(bundle.getString("sort.default"));
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applySorting());
    }

    @FXML
    private void changeLanguage(ActionEvent event) {
        Locale locale = languageCombo.getValue().equals("Français") ? Locale.FRENCH : Locale.ENGLISH;
        bundle = ResourceBundle.getBundle("lang/messages", locale);
        refreshUI();
    }

    private void refreshUI() {

        // Champs de recherche/filtre
        searchField.setPromptText(bundle.getString("search.prompt"));
        difficultyFilter.setPromptText(bundle.getString("filter.prompt"));
        sortComboBox.setPromptText(bundle.getString("sort.prompt"));
        returnButton.setText(bundle.getString("back.button"));

        // Filtres existants
        difficultyFilter.getItems().setAll(
                bundle.getString("filter.all"),
                bundle.getString("filter.easy"),
                bundle.getString("filter.medium"),
                bundle.getString("filter.advanced")
        );

        // Tris existants
        sortComboBox.getItems().setAll(
                bundle.getString("sort.default"),
                bundle.getString("sort.name.asc"),
                bundle.getString("sort.name.desc"),
                bundle.getString("sort.difficulty.asc"),
                bundle.getString("sort.difficulty.desc")
        );

        updatePagination();
    }

    private void loadCourses() {
        allCourses = FXCollections.observableArrayList(coursService.recuperer());
        filteredCourses = new FilteredList<>(allCourses, p -> true);
        sortedCourses = new SortedList<>(filteredCourses);
        sortedCourses.setComparator(Comparator.comparing(Cours::getId));
    }

    private void updateFilters() {
        filteredCourses.setPredicate(this::matchesFilters);
        applySorting();
        updatePagination();
    }

    private boolean matchesFilters(Cours cours) {
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String selectedDifficulty = difficultyFilter.getValue();

        boolean matchesSearch = searchText.isEmpty() ||
                cours.getNom().toLowerCase().contains(searchText) ||
                cours.getEtat().toLowerCase().contains(searchText);

        boolean matchesDifficulty = (selectedDifficulty == null ||
                selectedDifficulty.equals(bundle.getString("filter.all"))) ||
                cours.getEtat().equalsIgnoreCase(getDifficultyKey(selectedDifficulty));

        return matchesSearch && matchesDifficulty;
    }



    private String getDifficultyKey(String displayText) {
        if (displayText.equals(bundle.getString("filter.easy"))) return "Facile";
        if (displayText.equals(bundle.getString("filter.medium"))) return "Moyen";
        if (displayText.equals(bundle.getString("filter.advanced"))) return "Avancé";
        return "";
    }

    private void applySorting() {
        String sortOption = sortComboBox.getValue();
        if (sortOption == null) {
            sortOption = bundle.getString("sort.default");
        }

        Comparator<Cours> comparator;

        if (sortOption.equals(bundle.getString("sort.name.asc"))) {
            comparator = Comparator.comparing(Cours::getNom, String.CASE_INSENSITIVE_ORDER);
        } else if (sortOption.equals(bundle.getString("sort.name.desc"))) {
            comparator = Comparator.comparing(Cours::getNom, String.CASE_INSENSITIVE_ORDER).reversed();
        } else if (sortOption.equals(bundle.getString("sort.difficulty.asc"))) {
            comparator = Comparator.comparing(c -> getDifficultyLevel(c.getEtat()));
        } else if (sortOption.equals(bundle.getString("sort.difficulty.desc"))) {
            comparator = Comparator.comparing((Cours c) -> getDifficultyLevel(c.getEtat())).reversed();
        } else {
            comparator = Comparator.comparing(Cours::getId);
        }

        sortedCourses.setComparator(comparator);
        updatePagination();
    }


    private int getDifficultyLevel(String difficulty) {
        return switch (difficulty.toLowerCase()) {
            case "facile" -> 1;
            case "moyen" -> 2;
            case "avancé" -> 3;
            default -> 0;
        };
    }

    private void updatePagination() {
        int pageCount = calculatePageCount();
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private int calculatePageCount() {
        int itemCount = sortedCourses.size();
        return (itemCount == 0) ? 1 : (int) Math.ceil((double) itemCount / ITEMS_PER_PAGE);
    }

    private Node createPage(int pageIndex) {
        GridPane pageGrid = new GridPane();
        pageGrid.setHgap(20);
        pageGrid.setVgap(20);
        pageGrid.setAlignment(Pos.TOP_CENTER);
        pageGrid.setPadding(new Insets(10));

        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, sortedCourses.size());

        int row = 0, col = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            Cours cours = sortedCourses.get(i);
            pageGrid.add(createCourseCard(cours), col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
        return pageGrid;
    }

    private VBox createCourseCard(Cours cours) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);");
        box.setPrefWidth(280);
        box.setPrefHeight(150);

        Label name = new Label(cours.getNom());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Circle statusDot = new Circle(6);
        statusDot.setFill(getStatusColor(cours.getEtat()));

        Label state = new Label(MessageFormat.format(bundle.getString("course.state"), cours.getEtat()));
        state.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        statusBox.getChildren().addAll(statusDot, state);

        Button btn = new Button(bundle.getString("course.view.chapters"));
        btn.setStyle("-fx-background-color: #4a6baf; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15; -fx-cursor: hand;");
        btn.setOnAction(e -> navigateToChapters(cours));
        btn.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(name, statusBox, btn);
        return box;
    }

    private Color getStatusColor(String state) {
        return switch (state.toLowerCase()) {
            case "facile" -> Color.web("#2ecc71");
            case "moyen" -> Color.web("#f39c12");
            case "avancé" -> Color.web("#e74c3c");
            default -> Color.web("#3498db");
        };
    }

    private void navigateToChapters(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChapitreEtudiantView.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            ChapitreControllerEtudiant controller = loader.getController();
            controller.initData(cours, bundle);

            Stage stage = (Stage) gridCours.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(bundle.getString("error.load.chapters"));
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("error.title"));
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void returnToMainMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainMenu.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root); // Create Scene
            scene.getStylesheets().add(getClass().getResource("/styles/mainF.css").toExternalForm()); // Add CSS
            stage.setScene(scene);
            stage.setTitle("OUTI-D");
        } catch (IOException e) {
            showAlert(bundle.getString("error.load.home"));
        }
    }
}