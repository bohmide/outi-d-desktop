package services;

import entities.Event;
import entities.EventGenre;
import entities.Event_sponsors;
import entities.Sponsors;
import utils.MyConnection;

import java.sql.*;
import java.sql.Date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class EventService {

    static EventGenreService genreService = new EventGenreService();
    static SponsorsService sponsorsService = new SponsorsService();

    public void setEventSponsor(Event event, Sponsors sponsor) {
        try{
            String querry = "insert into evenements_sponsors values (?, ?)";

            PreparedStatement preStatement = MyConnection.getInstance().getCnx().prepareStatement(querry);

            preStatement.setInt(1, event.getId());
            preStatement.setInt(2, sponsor.getId());

            preStatement.executeUpdate();


        }catch (SQLException e){
            System.out.println("error setEventSponsor: "+e.getMessage());
        }
    }

    public void setEventSponsor(int eventId, int sponsorId) {
        try{
            String querry = "insert into evenements_sponsors values (?, ?)";

            PreparedStatement preStatement = MyConnection.getInstance().getCnx().prepareStatement(querry);

            preStatement.setInt(1, eventId);
            preStatement.setInt(2, sponsorId);

            preStatement.executeUpdate();


        }catch (SQLException e){
            System.out.println("error setEventSponsor: "+e.getMessage());
        }
    }


    public List<Event> getEventSponsors(List<Event> listEvents) {
        List<Event_sponsors> listEventSponsors = new ArrayList<>();

        try {
            String querry = "SELECT * FROM evenements_sponsors";

            Statement createStatement = MyConnection.getInstance().getCnx().createStatement();

            ResultSet resultSet = createStatement.executeQuery(querry);

            while (resultSet.next()) {
                listEventSponsors.add(new Event_sponsors(resultSet.getInt(1), resultSet.getInt(2)));
            }

            Map<Integer, List<Integer>> sponsorParEvent = listEventSponsors.stream()
                    .collect(
                            Collectors.groupingBy(
                                    Event_sponsors::getId_event,
                                    Collectors.mapping(Event_sponsors::getId_sponsor, Collectors.toList())
                            )
                    );


            listEvents.forEach(event -> {
                List<Sponsors> sponsors = sponsorParEvent.getOrDefault(event.getId(), new ArrayList<>())
                        .stream()
                        .map(sponsorId -> new SponsorsService().findSponsorById(sponsorId))
                        .collect(Collectors.toList());

                event.setListSponsors(sponsors);


            });
            return listEvents;
        } catch (SQLException e) {
            System.out.println("error getEventSponsor: "+e.getMessage());
        }

        return listEvents;
    }

    public List<Event> listEvenets() {
        List<Event> events = new ArrayList<>();
        try {
            String query = "SELECT * FROM evenements";
            Statement createStatement = MyConnection.getInstance().getCnx().createStatement();
            ResultSet resultSet = createStatement.executeQuery(query);

            while (resultSet.next()) {
                try {
                    // Get the ID safely
                    int eventId;
                    try {
                        eventId = resultSet.getInt(1);
                    } catch (Exception e) {
                        System.err.println("Invalid ID format: " + e.getMessage());
                        continue; // Skip this row entirely
                    }

                    // Safely get genre_id
                    int genreId;
                    try {
                        genreId = resultSet.getInt(2);
                    } catch (Exception e) {
                        System.err.println("Invalid genre_id for event " + eventId + ": " + e.getMessage());
                        genreId = 0; // Default value
                    }

                    // Get string fields
                    String nomEvent = resultSet.getString(3);
                    String description = resultSet.getString(4);

                    // Safely get dates
                    LocalDate dateEvent = null;
                    try {
                        dateEvent = convertToLocalDate(resultSet, 5);
                    } catch (Exception e) {
                        System.err.println("Invalid date_event for event " + eventId + ": " + e.getMessage());
                    }

                    LocalDate dateCreation = null;
                    try {
                        dateCreation = convertToLocalDate(resultSet, 8);
                    } catch (Exception e) {
                        System.err.println("Invalid date_creation for event " + eventId + ": " + e.getMessage());
                    }

                    // Safely get nbr_members
                    int nbrMembers;
                    try {
                        nbrMembers = resultSet.getInt(6);
                    } catch (Exception e) {
                        System.err.println("Invalid nbr_members for event " + eventId + ": " + e.getMessage());
                        nbrMembers = 0; // Default value
                    }

                    String imagePath = resultSet.getString(7);

                    // Safely get prix - THIS IS WHERE THE ERROR IS HAPPENING
                    float prix = 0.0f;
                    try {
                        // First try to get as float directly
                        prix = resultSet.getFloat(9);
                    } catch (Exception e) {
                        // If that fails, try to get as string and convert
                        try {
                            String prixStr = resultSet.getString(9);
                            if (prixStr != null && !prixStr.isEmpty()) {
                                try {
                                    prix = Float.parseFloat(prixStr);
                                } catch (NumberFormatException nfe) {
                                    System.err.println("Invalid prix value '" + prixStr + "' for event " + eventId + ": " + nfe.getMessage());
                                    // Keep the default 0.0f
                                }
                            }
                        } catch (Exception e2) {
                            System.err.println("Error getting prix for event " + eventId + ": " + e2.getMessage());
                            // Keep the default 0.0f
                        }
                    }

                    // Create the event with safe values
                    Event event = new Event(
                            eventId,
                            nomEvent != null ? nomEvent : "No Title",
                            description != null ? description : "No Description",
                            dateEvent != null ? dateEvent : LocalDate.now(),
                            nbrMembers,
                            imagePath != null ? imagePath : "",
                            dateCreation != null ? dateCreation : LocalDate.now(),
                            prix
                    );

                    // Safely get genre
                    EventGenre genre = null;
                    try {
                        genre = genreService.findGenreById(genreId);
                    } catch (Exception e) {
                        System.err.println("Error fetching genre for event " + eventId + ": " + e.getMessage());
                    }

                    if (genre == null) {
                        genre = new EventGenre();
                        genre.setNomGenre("Unknown");
                    }

                    event.setGenre(genre);
                    events.add(event);

                    System.out.println("Successfully loaded event ID " + eventId + " with prix: " + prix);
                } catch (Exception e) {
                    System.err.println("Error processing event row: " + e.getMessage());
                    e.printStackTrace();
                    // Continue to next row instead of failing the entire method
                }
            }
            System.out.println("Total events retrieved: " + events.size());
            return getEventSponsors(events);

        } catch (SQLException e) {
            System.err.println("Error in listEvenets: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // Helper method to handle different date formats
    private LocalDate convertToLocalDate(ResultSet rs, int columnIndex) throws SQLException {
        try {
            // First try to get as Date
            java.sql.Date sqlDate = rs.getDate(columnIndex);
            if (sqlDate != null) {
                return sqlDate.toLocalDate();
            }
        } catch (SQLException e) {
            // If Date fails, try as Timestamp
            try {
                java.sql.Timestamp timestamp = rs.getTimestamp(columnIndex);
                if (timestamp != null) {
                    return timestamp.toLocalDateTime().toLocalDate();
                }
            } catch (SQLException e2) {
                // If Timestamp fails, try as Long (milliseconds)
                try {
                    long millis = rs.getLong(columnIndex);
                    if (millis > 0) {
                        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                } catch (SQLException e3) {
                    // If all fail, try as String
                    try {
                        String dateStr = rs.getString(columnIndex);
                        if (dateStr != null && !dateStr.isEmpty()) {
                            return LocalDate.parse(dateStr);
                        }
                    } catch (Exception e4) {
                        System.err.println("Could not parse date at column " + columnIndex + ": " + e4.getMessage());
                    }
                }
            }
        }
        return null; // or throw exception if date is required
    }

    public void addEvent(Event event) {
        if (event.getGenre() == null) {
            System.out.println("Genre is null, cannot add event.");
            return;
        }
        if (event.getListSponsors() == null || event.getListSponsors().isEmpty()) {
            System.out.println("Sponsor list is empty, cannot add event.");
            return;
        }

        int genreId = event.getGenre().getId();

        try {
            // Insert event first and get generated ID
            String query = "INSERT INTO evenements (genre_id, nom_event, description, date_event, nbr_members, image_path, date_creation, prix) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preStatement = MyConnection.getInstance().getCnx().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preStatement.setInt(1, genreId);
            preStatement.setString(2, event.getNomEvent());
            preStatement.setString(3, event.getDescription());
            preStatement.setDate(4, Date.valueOf(event.getDateEvent()));
            preStatement.setInt(5, event.getNbrMemebers());
            preStatement.setString(6, event.getImagePath());
            preStatement.setDate(7, Date.valueOf(event.getDateCreation()));
            preStatement.setFloat(8, event.getPrix());

            preStatement.executeUpdate();

            ResultSet rs = preStatement.getGeneratedKeys();
            if (rs.next()) {
                int eventId = rs.getInt(1);
                event.setId(eventId);

                // Link all sponsors
                for (Sponsors sponsor : event.getListSponsors()) {
                    setEventSponsor(eventId, sponsor.getId());
                }
            }

        } catch (SQLException e) {
            System.out.println("error addEvent (multi-sponsor): " + e.getMessage());
        }
    }


    public void addEvent(Event event, int genreId, int sponsorId) {
        try{
            String query = "INSERT INTO evenements (genre_id, nom_event, description, date_event, nbr_members, image_path, date_creation, prix) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preStatement = MyConnection.getInstance().getCnx().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preStatement.setInt(1, genreId);
            preStatement.setString(2, event.getNomEvent());
            preStatement.setString(3, event.getDescription());
            preStatement.setDate(4, Date.valueOf(event.getDateEvent()));
            preStatement.setInt(5, event.getNbrMemebers());
            preStatement.setString(6, event.getImagePath());
            preStatement.setDate(7, Date.valueOf(event.getDateCreation()));
            preStatement.setFloat(8, event.getPrix());

            preStatement.executeUpdate();

            ResultSet rs = preStatement.getGeneratedKeys();
            if(rs.next()){
                int id = rs.getInt(1);
                event.setId(id);
            }

            setEventSponsor(event.getId(), sponsorId);

        }catch (SQLException e){
            System.out.println("error addEvent: "+e.toString());
        }
    }

    public Event findEventById(int id) {
        try {
            String query = "SELECT * FROM evenements WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getString("nom_event"),
                        rs.getString("description"),
                        rs.getDate("date_event").toLocalDate(),
                        rs.getInt("nbr_members"),
                        rs.getString("image_path"),
                        rs.getDate("date_creation").toLocalDate(),
                        rs.getFloat("prix")
                );
                EventGenre genre = genreService.findGenreById(rs.getInt("genre_id"));
                event.setGenre(genre);

                // Récupère les sponsors
                List<Event> eventList = new ArrayList<>();
                eventList.add(event);
                List<Event> enrichedEvents = getEventSponsors(eventList);

                return enrichedEvents.get(0);
            }
        } catch (SQLException e) {
            System.out.println("error findEventById: " + e.getMessage());
        }

        return null;
    }


    public void updateEvent(Event event) {
        try {
            String query = "UPDATE evenements SET genre_id = ?, nom_event = ?, description = ?, date_event = ?, nbr_members = ?, image_path = ?, date_creation = ?, prix = ? WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);

            pst.setInt(1, event.getGenre().getId());
            pst.setString(2, event.getNomEvent());
            pst.setString(3, event.getDescription());
            pst.setDate(4, Date.valueOf(event.getDateEvent()));
            pst.setInt(5, event.getNbrMemebers());
            pst.setString(6, event.getImagePath());
            pst.setDate(7, Date.valueOf(event.getDateCreation()));
            pst.setFloat(8, event.getPrix());
            pst.setInt(9, event.getId());

            pst.executeUpdate();

            System.out.println("Event updated successfully.");
        } catch (SQLException e) {
            System.out.println("error updateEvent: " + e.getMessage());
        }
    }


    public void deleteEvent(int eventId) {
        try {
            // Supprimer d'abord les relations sponsors (si contraintes FK)
            String deleteRelation = "DELETE FROM evenements_sponsors WHERE evenements_id = ?";
            PreparedStatement relPst = MyConnection.getInstance().getCnx().prepareStatement(deleteRelation);
            relPst.setInt(1, eventId);
            relPst.executeUpdate();

            // Puis supprimer l'événement
            String query = "DELETE FROM evenements WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, eventId);
            pst.executeUpdate();

            System.out.println("Event deleted successfully.");
        } catch (SQLException e) {
            System.out.println("error deleteEvent: " + e.getMessage());
        }
    }
}