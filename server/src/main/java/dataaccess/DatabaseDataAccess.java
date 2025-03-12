package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public class DatabaseDataAccess implements DataAccess {
    public DatabaseDataAccess() {

    }

    @Override
    public void clearData() throws DataAccessException {

    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData newGameData) throws DataAccessException {

    }
}
