package services;

import dao.GamesDAO;
import entities.Games;

import java.sql.SQLException;
import java.util.List;

public class GamesService {

    private final GamesDAO gamesDAO = new GamesDAO();

    public void add(Games game) throws SQLException {
        gamesDAO.add(game);
    }

    public List<Games> getAll() throws SQLException {
        return gamesDAO.getAll();
    }

    public void delete(int id) throws SQLException {
        gamesDAO.delete(id);
    }

    public void update(Games game) throws SQLException {
        gamesDAO.update(game);
    }
}
