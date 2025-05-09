package services;

import dao.BadgeDAO;
import entities.Badge;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class BadgeService {

    private final BadgeDAO badgeDAO;

    public BadgeService() {
        this.badgeDAO = new BadgeDAO();
    }

    public void add(Badge badge) throws SQLException {
        badgeDAO.add(badge);
    }

    public List<Badge> getAll() throws SQLException {
        return badgeDAO.getAll();
    }

    public void delete(int id) throws SQLException {
        badgeDAO.delete(id);
    }

    public void update(Badge badge) throws SQLException {
        badgeDAO.update(badge);
    }

    public Badge getById(int id) throws SQLException {
        return badgeDAO.getById(id);
    }

    public Badge getByName(String name) throws SQLException {
        return badgeDAO.getByName(name);
    }
    public List<Badge> getUnlockedBadges(int totalScore) throws SQLException {
        List<Badge> allBadges = getAll();
        return allBadges.stream()
                .filter(b -> totalScore >= b.getRequiredScore())
                .collect(Collectors.toList());
    }

}
