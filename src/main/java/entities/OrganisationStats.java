package entities;

public class OrganisationStats {
    private Organisation organisation;
    private int teamCount;

    public OrganisationStats(Organisation organisation, int teamCount) {
        this.organisation = organisation;
        this.teamCount = teamCount;
    }

    // Getters
    public Organisation getOrganisation() { return organisation; }
    public int getTeamCount() { return teamCount; }
}