package interfaces;

import entities.Organisation;
import java.util.List;

public interface IServiceOrganisation {
    void ajouterOrganisation(Organisation org);
    List<Organisation> afficherOrganisations();
    boolean supprimerOrganisation(int id);
    void modifierOrganisation(Organisation org);

}
