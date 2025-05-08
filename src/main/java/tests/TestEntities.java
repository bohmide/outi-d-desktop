package tests;

import entities.Event;
import entities.EventGenre;
import entities.Sponsors;
import services.EventGenreService;
import services.EventService;
import services.SponsorsService;

import java.time.LocalDate;
import java.util.*;

public class TestEntities {

    static Scanner sc = new Scanner(System.in);

    // SERVICES
    static EventService eventService = new EventService();
    static EventGenreService genreService = new EventGenreService();
    static SponsorsService sponsorsService = new SponsorsService();

    public static void main(String[] args) {

        boolean run = true;

        while (run) {
            System.out.println("""
                    Hello!!
                    
                    1: Evenements.
                    2: Genres Evenements.
                    3: Sponsors.
                    0: Quitter.
                    """);
            int choice = sc.nextInt();

            switch (choice) {
                case 0: {
                    run = false;

                    break;
                }

                case 1: {
                    System.out.println("levenement mazel matekhdemech arja3 ghodwa");
                    eventMenu();
                    break;
                }

                case 2: {
                    System.out.println("Genres Evenement Options\n");
                    genreEvenementMenu();
                    break;
                }

                case 3: {
                    System.out.println("Sponsors Options\n");
                    sponsorMenu();
                    break;
                }
            }
        }
    }

    static void eventMenu() {
        boolean run = true;

        while (run) {
            System.out.println("""
                    Evenement Menu
                    
                    1: Ajouter.
                    2: Liste.
                    3: Trouver.
                    4: Mettre à jour.
                    5: Supprimer.
                    0: Quitter.
                    
                    """);

            int choice = sc.nextInt();

            switch (choice) {
                case 0: {
                    run = false;

                    break;
                }

                case 1: {
                    System.out.println("Ajouter un Evenement\n matekhdemech");
                    sc.nextLine();

                    System.out.print("nom de l'evenement: ");
                    String nomEvenement = sc.nextLine();

                    System.out.print("Description: ");
                    String description = sc.nextLine();

                    LocalDate date_event = LocalDate.now().plusDays(2);

                    System.out.print("nombre de memebre limite");
                    int nbrMemebre = sc.nextInt();

                    sc.nextLine();

                    System.out.print("image path: ");
                    String imagePathEvenement = sc.nextLine();

                    LocalDate dateCreation = LocalDate.now();

                    System.out.print("Prix: ");
                    float prix = sc.nextFloat();

                    System.out.print("genre Id: ");
                    int genreId = sc.nextInt();

                    System.out.print("sponsor Id: ");
                    int sponsorId = sc.nextInt();

                    eventService.addEvent(new Event(nomEvenement, description, date_event, nbrMemebre, imagePathEvenement, dateCreation, prix), genreId, sponsorId);
                    break;
                }

                case 2: {
                    System.out.println("Liste des Evenements:\n");
                    List<Event> events = eventService.listEvenets();
                    if (events == null) {
                        System.out.println("Auccain Event trouver");
                    } else {
                        for (Event e : events) {
                            System.out.println(e);
                        }
                    }
                    break;
                }

                case 3: {
                    System.out.print("""
                            Trouver un Evenement par ID: 
                            """);


                    int id = sc.nextInt();

                    Event e = eventService.findEventById(id);
                    System.out.println(e != null ? e : "Sponsor non trouvé.");

                    break;

                }

                case 4: {
                    System.out.println("""
                                Mettre à jour un Événement par ID:
                            """);

                    System.out.print("ID de l'événement: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    Event eventToUpdate = eventService.findEventById(id); // méthode à ajouter si pas encore faite

                    if (eventToUpdate != null) {
                        System.out.print("Nouveau nom: ");
                        String nom = sc.nextLine();

                        System.out.print("Nouvelle description: ");
                        String desc = sc.nextLine();

                        System.out.print("Nouvelle date (yyyy-mm-dd): ");
                        String dateEventStr = sc.nextLine();
                        LocalDate dateEvent = LocalDate.parse(dateEventStr);

                        System.out.print("Nouveau nombre de membres: ");
                        int nbr = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Nouveau chemin de l'image: ");
                        String path = sc.nextLine();

                        System.out.print("Nouvelle date de création (yyyy-mm-dd): ");
                        String dateCreationStr = sc.nextLine();
                        LocalDate dateCreation = LocalDate.parse(dateCreationStr);

                        System.out.print("Nouveau prix: ");
                        float prix = sc.nextFloat();
                        sc.nextLine();

                        eventToUpdate.setNomEvent(nom);
                        eventToUpdate.setDescription(desc);
                        eventToUpdate.setDateEvent(dateEvent);
                        eventToUpdate.setNbrMemebers(nbr);
                        eventToUpdate.setImagePath(path);
                        eventToUpdate.setDateCreation(dateCreation);
                        eventToUpdate.setPrix(prix);

                        eventService.updateEvent(eventToUpdate); // méthode déjà partagée plus haut
                    } else {
                        System.out.println("Événement non trouvé.");
                    }

                    break;
                }

                case 5: {
                    System.out.println("""
                                Supprimer un Événement par ID 
                            """);


                    int id = sc.nextInt();
                    sc.nextLine();

                    Event eventToDelete = eventService.findEventById(id);

                    if (eventToDelete != null) {
                        eventService.deleteEvent(id); // méthode déjà fournie plus haut
                        System.out.println("Événement supprimé.");
                    } else {
                        System.out.println("Événement non trouvé.");
                    }


                    break;
                }


                default: {
                    System.out.println("Option invalide.");

                    break;
                }
            }
        }
    }


    static void sponsorMenu() {
        boolean run = true;

        while (run) {
            System.out.println("""
                    Sponsors Menu
                    
                    1: Ajouter.
                    2: Liste.
                    3: Trouver.
                    4: Mettre à jour.
                    5: Supprimer.
                    0: Quitter.
                    
                    """);

            int choice = sc.nextInt();

            switch (choice) {
                case 0: {
                    run = false;

                    break;
                }

                case 1: {
                    System.out.println("Ajouter un sponsor\n");
                    sc.nextLine();


                    System.out.print("Nom du sponsor: ");
                    String nom = sc.nextLine();

                    System.out.print("Description: ");
                    String desc = sc.nextLine();

                    System.out.print("Image Path: ");
                    String path = sc.nextLine();

                    Sponsors sponsor = new Sponsors(nom, desc, path, LocalDate.now());
                    sponsorsService.addSponsor(sponsor);

                    break;
                }

                case 2: {
                    System.out.println("Liste des sponsors:\n");

                    List<Sponsors> sponsorsList = sponsorsService.listSponsor();
                    if (sponsorsList != null) {
                        for (Sponsors s : sponsorsList) System.out.println(s);
                    } else {
                        System.out.println("Auccuin sponsors:\n");
                    }

                    break;
                }

                case 3: {
                    System.out.println("""
                            Trouver un sponsor:
                            
                            1: Par ID
                            2: Par nom
                            """);

                    int findChoice = sc.nextInt();
                    sc.nextLine();

                    switch (findChoice) {
                        case 1: {
                            System.out.print("ID du sponsor: ");
                            int id = sc.nextInt();

                            Sponsors s = sponsorsService.findSponsorById(id);
                            System.out.println(s != null ? s : "Sponsor non trouvé.");

                            break;
                        }

                        case 2: {
                            System.out.print("Nom du sponsor: ");
                            String name = sc.nextLine();

                            Sponsors s = sponsorsService.findSponsorByName(name);
                            System.out.println(s != null ? s : "Sponsor non trouvé.");

                            break;
                        }
                    }

                    break;
                }

                case 4: {
                    System.out.println("""
                            Mettre à jour un sponsor:
                            
                            1: Par ID
                            2: Par nom
                            """);

                    int updateChoice = sc.nextInt();
                    sc.nextLine();

                    Sponsors sponsorToUpdate = null;

                    if (updateChoice == 1) {
                        System.out.print("ID du sponsor: ");
                        int id = sc.nextInt();
                        sponsorToUpdate = sponsorsService.findSponsorById(id);
                        sc.nextLine();
                    } else if (updateChoice == 2) {
                        System.out.print("Nom du sponsor: ");
                        String name = sc.nextLine();
                        sponsorToUpdate = sponsorsService.findSponsorByName(name);
                    }

                    if (sponsorToUpdate != null) {
                        System.out.print("Nouveau nom: ");
                        String nom = sc.nextLine();

                        System.out.print("Nouvelle description: ");
                        String desc = sc.nextLine();

                        System.out.print("Nouveau image path: ");
                        String path = sc.nextLine();

                        sponsorToUpdate.setNomSponsor(nom);
                        sponsorToUpdate.setDescription(desc);
                        sponsorToUpdate.setImagePath(path);

                        sponsorsService.updateSponsor(sponsorToUpdate);
                    } else {
                        System.out.println("Sponsor non trouvé.");
                    }

                    break;
                }

                case 5: {
                    System.out.println("""
                            Supprimer un sponsor:
                            
                            1: Par ID
                            2: Par nom
                            """);

                    int deleteChoice = sc.nextInt();
                    sc.nextLine();

                    Sponsors sponsorToDelete = null;

                    if (deleteChoice == 1) {
                        System.out.print("ID du sponsor: ");
                        int id = sc.nextInt();
                        sponsorToDelete = sponsorsService.findSponsorById(id);
                        sc.nextLine();
                    } else if (deleteChoice == 2) {
                        System.out.print("Nom du sponsor: ");
                        String name = sc.nextLine();
                        sponsorToDelete = sponsorsService.findSponsorByName(name);
                    }

                    if (sponsorToDelete != null) {

                        sponsorsService.updateSponsor(sponsorToDelete);
                    } else {
                        System.out.println("Sponsor non trouvé.");
                    }

                    break;

                }

                default: {
                    System.out.println("Option invalide.");

                    break;
                }
            }
        }
    }


    static void genreEvenementMenu() {
        boolean run = true;

        while (run) {
            System.out.println("""
                    Genres Evenement Menu
                    
                    1: Ajouter
                    2: Lister
                    3: Trouver
                    4: Mettre à jour
                    0: Quitter
                    
                    """);

            int choice = sc.nextInt();

            switch (choice) {
                case 0: {
                    run = false;

                    break;
                }

                case 1: {
                    System.out.println("Ajouter un genre\n");
                    sc.nextLine(); // Flush

                    System.out.print("Nom du genre: ");
                    String nom = sc.nextLine();

                    System.out.print("Chemin (path): ");
                    String path = sc.nextLine();

                    EventGenre genre = new EventGenre(nom, 0, path, LocalDate.now());
                    genreService.addEventGenre(genre);

                    break;
                }

                case 2: {
                    System.out.println("Liste des genres:\n");

                    List<EventGenre> genres = genreService.listEventGenre();
                    for (EventGenre g : genres) System.out.println(g);

                    break;
                }

                case 3: {
                    System.out.println("""
                            Trouver un genre:
                            
                            1: Par ID
                            2: Par nom
                            """);

                    int findChoice = sc.nextInt();
                    sc.nextLine();

                    switch (findChoice) {
                        case 1: {
                            System.out.print("ID du genre: ");
                            int id = sc.nextInt();
                            System.out.println(genreService.findGenreById(id));

                            break;
                        }

                        case 2: {
                            System.out.print("Nom du genre: ");
                            String nom = sc.nextLine();
                            System.out.println(genreService.findGenreByName(nom));

                            break;
                        }

                        default:
                            System.out.println("Option invalide.");
                    }

                    break;
                }

                case 4: {
                    System.out.println("""
                            Mettre à jour un genre:
                            
                            1: Par ID
                            2: Par nom
                            """);

                    int updateChoice = sc.nextInt();
                    sc.nextLine();

                    EventGenre genre = null;

                    if (updateChoice == 1) {
                        System.out.print("ID du genre: ");
                        int id = sc.nextInt();
                        genre = genreService.findGenreById(id);
                        sc.nextLine();
                    } else if (updateChoice == 2) {
                        System.out.print("Nom du genre: ");
                        String nom = sc.nextLine();
                        genre = genreService.findGenreByName(nom);
                    }

                    if (genre != null) {
                        System.out.print("Nouveau nom du genre: ");
                        String newName = sc.nextLine();

                        System.out.print("Nouveau chemin (path): ");
                        String newPath = sc.nextLine();

                        genre.setNomGenre(newName);
                        genre.setImagePath(newPath);

                        genreService.updateEventGenre(genre);
                    } else {
                        System.out.println("Genre non trouvé.");
                    }

                    break;
                }

                default: {
                    System.out.println("Option invalide.");

                    break;
                }
            }
        }
    }


}
