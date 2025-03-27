package tests;

import entities.EventGenre;
import services.EvenetGenreService;

import java.time.LocalDate;
import java.util.*;

public class TestEntities {
    public static void main(String[] args) {
        //EventGenre genre = new EventGenre(
          //      "test genre 2", 0, "path", LocalDate.now());

        EvenetGenreService genreService = new EvenetGenreService();

        //genreService.addEntity(genre);

        //List<EventGenre> listgenre = genreService.listEntity();
        //System.out.println(listgenre);

        //genreService.deleteEntityByName("test genre 1");
        EventGenre genre1 = genreService.findGenreByName("test genre 2");

        genreService.updateEntityById(genre1);

    }
}
