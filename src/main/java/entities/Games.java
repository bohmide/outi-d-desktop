package entities;

public class Games {

    private int id;
    private String name;

    public Games() {
    }

    public Games(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Games(String name) {
        this.name = name;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString pour affichage
    @Override
    public String toString() {
        return name;
    }
}
