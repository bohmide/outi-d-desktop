package services;

public class Event_sponsors {

    private int id;
    private int id_event;
    private int id_sponsor;

    public Event_sponsors(int id, int id_event, int id_sponsor) {
        this.id = id;
        this.id_event = id_event;
        this.id_sponsor = id_sponsor;
    }

    public int getId() { return id; }
    public int getId_event() { return id_event; }
    public int getId_sponsor() { return id_sponsor; }
}
