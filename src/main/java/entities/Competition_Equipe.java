package entities;

import javafx.beans.property.*;

public class Competition_Equipe {
    private IntegerProperty id = new SimpleIntegerProperty();
    private IntegerProperty competitionId = new SimpleIntegerProperty();
    private IntegerProperty equipeId = new SimpleIntegerProperty();

    public Competition_Equipe() {}

    public Competition_Equipe(int id, int competitionId, int equipeId) {
        this.id.set(id);
        this.competitionId.set(competitionId);
        this.equipeId.set(equipeId);
    }

    // Constructeur sans id, pour créer l'association sans spécifier l'ID (ID auto-généré)
    public Competition_Equipe(int competitionId, int equipeId) {
        this.competitionId.set(competitionId);  // Utilisation de .set() pour affecter la valeur
        this.equipeId.set(equipeId);  // Utilisation de .set() pour affecter la valeur
    }


    // Getters/Setters
    public int getId() { return id.get(); }
    public int getCompetitionId() { return competitionId.get(); }
    public int getEquipeId() { return equipeId.get(); }

    public void setId(int id) { this.id.set(id); }
    public void setCompetitionId(int id) { this.competitionId.set(id); }
    public void setEquipeId(int id) { this.equipeId.set(id); }
}