package dataaccess;

import chess.ChessGame;
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

    private final String[] setupStrings = {"INSERT INTO users (username, passhash,  email) VALUES ('" + existingUser.username() + "', '" + existingUser.password() + "', '" + existingUser.email() + "')", "INSERT INTO authdata (authtoken, username) VALUES('testauth', '" + existingUser.username() + "')",

    };

    @BeforeAll
    static void init() {
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
        existingGame = new GameData(-1, null, null, "existingGame", new ChessGame());
        try {
            database = new DatabaseDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
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


}
