package tests;

import entities.EventGenre;
import services.EventGenreService;

import java.time.LocalDate;
import java.util.*;

public class TestEntities {
    public static void main(String[] args) {
        EventGenre genre = new EventGenre(
                "test genre 2", 0, "path", LocalDate.now());

        EventGenreService genreService = new EventGenreService();

    }
}
