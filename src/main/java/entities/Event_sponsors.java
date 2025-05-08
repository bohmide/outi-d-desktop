package entities;

public class Event_sponsors {
    int id_event;
    int id_sponsor;

    public Event_sponsors(){}

    public Event_sponsors(int id_event, int id_sponsor) {
        this.id_event = id_event;
        this.id_sponsor = id_sponsor;
    }

    public int getId_sponsor() {
        return id_sponsor;
    }

    public void setId_sponsor(int id_sponsor) {
        this.id_sponsor = id_sponsor;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }
}
