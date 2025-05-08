package interfaces;

import entities.Competition;
import entities.OrganisationStats;
import java.util.List;

public interface IServiceCompetition {
    void ajouterCompetition(Competition comp);
    List<Competition> afficherCompetitions();
    void supprimerCompetition(int id);
    void modifierCompetition(Competition comp);

    List<OrganisationStats> getTopOrganisationsWithTeamCount(int limit);
}
