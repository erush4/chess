package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataAccessTests {
    static DatabaseDataAccess database;
    static UserData existingUser;
    static UserData newUser;
    static GameData existingGame;
    static AuthData existingAuth;
    static AuthData newAuth;
    static String gameJson;

    private final String[] setupStrings = {
            "INSERT INTO users (username, passhash,  email) VALUES ("
                    + nullCorrect(existingUser.username()) + ", "
                    + nullCorrect(existingUser.password()) + ", "
                    + nullCorrect(existingUser.email()) + ")",
            "INSERT INTO authdata (authtoken, username) VALUES("
                    + nullCorrect(existingAuth.authToken()) + ", "
                    + nullCorrect(existingAuth.username()) + ")",
            "INSERT INTO  games (gameid, whiteusername, blackusername, gamename, gamejson) Values('"
                    + existingGame.gameID() + "', "
                    + nullCorrect(existingGame.whiteUsername()) + ", "
                    + nullCorrect(existingGame.blackUsername()) + ", "
                    + nullCorrect(existingGame.gameName()) + ", "
                    + nullCorrect(gameJson) + ")"
    };

    @BeforeAll
    static void init() {
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
        existingGame = new GameData(-1, null, null, "existingGame", new ChessGame());
        existingAuth = new AuthData("testAuth", existingUser.username());
        newAuth = new AuthData("newAuth", newUser.username());
        gameJson = new Gson().toJson(existingGame.game());
        try {
            database = new DatabaseDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String nullCorrect(String input) {
        if (input == null) {
            return "NULL";
        } else {
            return "'" + input + "'";
        }
    }

    @AfterEach
    public void reset() {
        try {
            database.clearData();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    public void setup() {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : setupStrings) {
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("getUser on Valid User Succeeds")
    void getValidUser() {
        try {
            var expected = existingUser;
            var actual = database.getUser(existingUser.username());
            Assertions.assertEquals(expected, actual);
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("getUser on Invalid User Fails")
    void getInvalidUser() {
        try {
            Assertions.assertNull(database.getUser(newUser.username()));
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("getAuth on Valid Auth Succeeds")
    void getValidAuth() {
        try {
            var expected = existingAuth;
            var actual = database.getAuth(existingAuth.authToken());
            Assertions.assertEquals(expected, actual);
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("getAuth on Invalid Auth Fails")
    void getInvalidAuth() {
        try {
            Assertions.assertNull(database.getAuth(newAuth.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("createUser Succeeds on Valid Input")
    void addGoodUser() {
        try {
            database.createUser(newUser);
            var expected = newUser;
            var actual = database.getUser(newUser.username());
            Assertions.assertEquals(expected, actual);
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("createUser Fails On Bad Input")
    void addBadUser() {
        var badUser = new UserData(null, null, null);
        Assertions.assertThrows(DataAccessException.class, () -> database.createUser(badUser));
    }

    @Test
    @DisplayName("createAuth Succeeds on Valid Input")
    void addGoodAuth() {
        try {
            database.createAuth(newAuth);
            var expected = newAuth;
            var actual = database.getAuth(newAuth.authToken());
            Assertions.assertEquals(expected, actual);
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("createAuth Fails On Bad Input")
    void addBadAuth() {
        var badAuth = new AuthData(null, null);
        Assertions.assertThrows(DataAccessException.class, () -> database.createAuth(badAuth));
    }

    @Test
    @DisplayName("getGame Succeeds on Valid Input")
    void getGoodGame() {
        try {
            var expected = existingGame;
            var actual = database.getGame(existingGame.gameID());
            Assertions.assertEquals(expected, actual);
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("getGame Fails on Invalid Input")
    void getBadGame() {
        try {
            GameData actual = database.getGame(1234);
            Assertions.assertNull(actual);
        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
    }


}
