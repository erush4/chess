package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashSet<AuthData> authDataSet = new HashSet<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clearData() {
        users.clear();
        authDataSet.clear();
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public void deleteAuth(AuthData authData) {

    }

    @Override
    public void getAuth(String authToken) {

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
