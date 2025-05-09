package entities;

import java.util.List;

public class Puzzle {
    private int id;
    private int gameId;
    private String finalImage;
    private List<String> pieces;
    private String gameName;

    public Puzzle() {}

    public Puzzle(int id, int gameId, String finalImage, List<String> pieces) {
        this.id = id;
        this.gameId = gameId;
        this.finalImage = finalImage;
        this.pieces = pieces;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }

    public String getFinalImage() { return finalImage; }
    public void setFinalImage(String finalImage) { this.finalImage = finalImage; }

    public List<String> getPieces() { return pieces; }
    public void setPieces(List<String> pieces) { this.pieces = pieces; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
}
