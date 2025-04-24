package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("BackOffice - OUTID");

        initRootLayout();
        showWelcomeScreen();
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();

        // Création d'une barre de menu
        MenuBar menuBar = new MenuBar();

        // Menu Gamification
        Menu gamificationMenu = new Menu("Gamification");

        MenuItem quizKidsItem = new MenuItem("QuizKids");
        quizKidsItem.setOnAction(e -> showQuizKids());

        MenuItem puzzleItem = new MenuItem("Puzzle");
        puzzleItem.setOnAction(e -> showPuzzle());

        MenuItem memoryCardItem = new MenuItem("Memory Card");
        memoryCardItem.setOnAction(e -> showMemoryCard());

        MenuItem gameItem = new MenuItem("Game");
        gameItem.setOnAction(e -> showGames());

        MenuItem badgeItem = new MenuItem("Badge");
        badgeItem.setOnAction(e -> showBadges());

        gamificationMenu.getItems().addAll(quizKidsItem, puzzleItem, memoryCardItem, gameItem,badgeItem);

        // Ajouter tous les menus à la barre
        menuBar.getMenus().addAll(gamificationMenu);

        rootLayout.setTop(menuBar);
        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showBadges() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/badge_view.fxml"));
            Parent badgeView = loader.load();
            rootLayout.setCenter(badgeView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWelcomeScreen() {
        // Une vue d’accueil simple ou un label
        rootLayout.setCenter(new javafx.scene.control.Label("Bienvenue dans le BackOffice OUTID ! Choisissez une section dans le menu."));
    }

    private void showQuizKids() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz_kids_view.fxml"));
            Parent quizView = loader.load();
            rootLayout.setCenter(quizView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showGames() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/games_view.fxml"));
            Parent gamesView = loader.load();
            rootLayout.setCenter(gamesView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showMemoryCard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/memory_card_view.fxml"));
            Parent view = loader.load();
            rootLayout.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showPuzzle() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puzzle_view.fxml"));
            Parent puzzleView = loader.load();
            rootLayout.setCenter(puzzleView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
