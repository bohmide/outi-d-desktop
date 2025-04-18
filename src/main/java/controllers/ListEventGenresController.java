package controllers;

import entities.EventGenre;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import services.EventGenreService;

import java.util.List;

public class ListEventGenresController {

    @FXML
    private ListView<String> genreListView;

    private EventGenreService eventGenreService = new EventGenreService();

    @FXML
    public void initialize() {
        List<EventGenre> genreList = eventGenreService.listEventGenre();
        for (EventGenre genre : genreList) {
            genreListView.getItems().add(genre.getNomGenre());
        }
    }
}
