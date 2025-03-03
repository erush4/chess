package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> authDataSet = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryDataAccess that = (MemoryDataAccess) o;
        return Objects.equals(users, that.users) && Objects.equals(authDataSet, that.authDataSet) && Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, authDataSet, games);
    }

    @Override
    public void clearData() {
        users.clear();
        authDataSet.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData userData){
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData authData) {
        authDataSet.put(authData.authToken(), authData);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        authDataSet.remove(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDataSet.get(authToken);
    }

    @Override
    public List<GameData> listGames() {
        return List.of();
    }

    @Override
    public void addGame(GameData gameData) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData newGameData) {

    }
}
