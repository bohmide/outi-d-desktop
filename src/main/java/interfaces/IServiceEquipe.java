package interfaces;

import entities.Equipe;
import entities.Competition_Equipe;
import java.util.List;

public interface IServiceEquipe {
    int ajouterEquipe(Equipe equipe);

    void supprimerEquipe(int id);
    List<Equipe> afficherToutesEquipes();
    boolean ajouterParticipation(Competition_Equipe ce);
    List<Equipe> getEquipesParCompetition(int competitionId);


}