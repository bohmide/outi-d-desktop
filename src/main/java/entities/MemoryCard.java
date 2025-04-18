package entities;

import java.util.List;

public class MemoryCard {
    private int id;
    private int gameId;
    private List<String> images; // Chemins des fichiers images
    private String gameName;

    public MemoryCard(int id, int gameId, List<String> images) {
        this.id = id;
        this.gameId = gameId;
        this.images = images;
    }

    public MemoryCard(int gameId, List<String> images) {
        this.gameId = gameId;
        this.images = images;
    }
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public MemoryCard() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}