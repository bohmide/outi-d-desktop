package entities;

public class Badge {
    private int id;
    private String name;
    private int requiredScore;
    private String icon; // Chemin vers l'icône

    public Badge() {
    }

    public Badge(String name, int requiredScore, String icon) {
        this.name = name;
        this.requiredScore = requiredScore;
        this.icon = icon;
    }

    public Badge(int id, String name, int requiredScore, String icon) {
        this.id = id;
        this.name = name;
        this.requiredScore = requiredScore;
        this.icon = icon;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getRequiredScore() { return requiredScore; }
    public void setRequiredScore(int requiredScore) { this.requiredScore = requiredScore; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    @Override
    public String toString() {
        return name;
    }
}
