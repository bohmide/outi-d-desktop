package tests;

import entities.EventGenre;
import entities.Sponsors;
import services.EventGenreService;
import services.SponsorsService;

import java.time.LocalDate;
import java.util.*;

public class TestEntities {
    public static void main(String[] args) {
        Sponsors newSponsor = new Sponsors(
                "test sponsor 1",
                "description",
                "path",
                LocalDate.now()
        );

        SponsorsService sponsorsService = new SponsorsService();

        //sponsorsService.addSponsor(newSponsor);
        //newSponsor.setNomSponsor("description 2");
        //System.out.println(newSponsor);
        //sponsorsService.updateSponsor(newSponsor);



        EventGenreService eventGenreService = new EventGenreService();

        EventGenre newEventGenre = new EventGenre(
                1,
                "test genre 2",
                0,
                "path",
                LocalDate.now()
        );

        EventGenre newEventGenre2 = eventGenreService.findGenreById(newEventGenre);


        eventGenreService.updateEventGenreById(newEventGenre);

    }
}
