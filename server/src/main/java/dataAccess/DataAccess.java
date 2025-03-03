package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess {
    void clearData() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;

    void deleteAuth(AuthData authData) throws DataAccessException;

    void getAuth(String authToken) throws DataAccessException;

    List<ChessGame> listGames() throws DataAccessException;

    void addGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData newGameData) throws DataAccessException;
}
