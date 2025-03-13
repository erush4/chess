package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class DatabaseDataAccess implements DataAccess {
    public DatabaseDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearData() throws DataAccessException {
        updateDatabase("TRUNCATE TABLE users");
        updateDatabase("TRUNCATE TABLE games");
        updateDatabase("TRUNCATE TABLE authdata");
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO users (username, passhash, email) VALUES(?,?,?)";
        updateDatabase(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        ResultSet rs = getData("SELECT * FROM users WHERE username=?", username);
        try {
            if (rs.next()) {
                String gotUsername = rs.getString("username");
                String passhash = rs.getString("gotPasshash");
                String email = rs.getString("email");
                return new UserData(gotUsername, passhash, email);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Could not get data: " + e.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO authdata (authtoken, username) VALUES (?, ?, ?)";
        updateDatabase(statement, authData.authToken(), authData.username());
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        ResultSet rs = getData("SELECT * FROM authdata WHERE authtoken=?", authToken);
        try {
            if (rs.next()) {
                String gotAuth = rs.getString("authtoken");
                String username = rs.getString("username");
                return new AuthData(gotAuth, username);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Could not get data: " + e.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> gameList = new ArrayList<GameData>();
        ResultSet rs = getData("SELECT * FROM games");
        try {
            while (rs.next()) {
                int gotGameID = rs.getInt("gameid");
                String whiteUsername = rs.getString("whiteusername");
                String blackUsername = rs.getString("blackusername");
                String gameName = rs.getString("gamename");
                String gameJSON = rs.getString("gamejson");
                ChessGame game = new Gson().fromJson(gameJSON, ChessGame.class);
                gameList.add(new GameData(gotGameID, whiteUsername, blackUsername, gameName, game));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not get data: " + e.getMessage());
        }
        return gameList;
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        ResultSet rs = getData("SELECT * FROM authdata WHERE gameId=?", gameID);
        try {
            if (rs.next()) {
                int gotGameID = rs.getInt("gameid");
                String whiteUsername = rs.getString("whiteusername");
                String blackUsername = rs.getString("blackusername");
                String gameName = rs.getString("gamename");
                String gameJSON = rs.getString("gamejson");
                ChessGame game = new Gson().fromJson(gameJSON, ChessGame.class);
                return new GameData(gotGameID, whiteUsername, blackUsername, gameName, game);
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Could not get data: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData newGameData) throws DataAccessException {

    }

    private void updateDatabase(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                prepareStatement(ps, params);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private ResultSet getData(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                prepareStatement(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not read data: " + e.getMessage());
        }
    }

    private void prepareStatement(PreparedStatement ps, Object[] params) throws SQLException, DataAccessException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            switch (param) {
                case Integer p -> ps.setInt(i + 1, p);
                case String p -> ps.setString(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> throw new DataAccessException("Bad parameter type: " + param.getClass().getName());
            }
        }
    }

    private final String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS users (
               username VARCHAR(32),
               passhash VARCHAR(64),
               email VARCHAR(64),
               PRIMARY KEY (username)
            )
            CREATE TABLE IF NOT EXISTS games (
                gameid VARCHAR(64),
                whiteusername VARCHAR(32),
                blackusername VARCHAR(32),
                gamename VARCHAR(32),
                gamejson TEXT
                )
            CREATE TABLE IF NOT EXISTS authdata (
                authtoken VARCHAR(64)
                username VARCHAR(32)
            )"""};

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Could not configure database: " + ex.getMessage());
        }
    }
}
